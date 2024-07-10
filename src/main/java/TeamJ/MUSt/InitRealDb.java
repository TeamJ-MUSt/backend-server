package TeamJ.MUSt;

import TeamJ.MUSt.domain.*;
import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.repository.WordRepository;
import TeamJ.MUSt.service.QuizService;
import TeamJ.MUSt.service.song.SongInfo;
import TeamJ.MUSt.util.BugsCrawler;
import TeamJ.MUSt.util.WordExtractor;
import TeamJ.MUSt.util.WordInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//@Component
@RequiredArgsConstructor
public class InitRealDb {

    private final InitService initService;

    private static Song createSampleSong(String title, String artist) throws IOException, NoSearchResultException {
        List<SongInfo> songInfos = BugsCrawler.callBugsApi(title, artist);
        SongInfo firstSong = songInfos.get(0);
        return new Song(
                title,
                artist.replaceAll("\\(.*?\\)", ""),
                imageToByte(firstSong.getThumbnailUrl()),
                imageToByte(firstSong.getThumbnailUrl_large()),
                firstSong.getMusic_id());
    }

    public static byte[] imageToByte(String imageURL) {
        byte[] imageBytes = null;
        try {
            URL url = new URL(imageURL);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;
            InputStream stream = url.openStream();

            while ((bytesRead = stream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }

            imageBytes = outputStream.toByteArray();

            stream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes;
    }

    @PostConstruct
    public void init() throws IOException, NoSearchResultException {
        initService.initDb();
    }

    //@Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        private final WordExtractor wordExtractor;
        private final WordRepository wordRepository;
        private final QuizService quizService;

        public void initDb() throws IOException, NoSearchResultException {
            Member[] members = new Member[1];
            members[0] = new Member("member1", "0");

            for (Member member : members)
                em.persist(member);

            String str = "";
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\saree98\\intellij-workspace\\MUSt\\src\\main\\resources\\song-list.txt"));
            while ((str = br.readLine()) != null)
                sb.append(str);
            ObjectMapper mapper = new ObjectMapper();
            List<Inputs> inputs = new ArrayList<>();
            try {
                inputs = mapper.readValue(sb.toString(), new TypeReference<List<Inputs>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<Song> songs = new ArrayList<>();
            for (Inputs input : inputs) {
                try {
                    Song sampleSong = createSampleSong(input.getTitle(), input.getArtist());
                    songs.add(sampleSong);
                } catch (NoSearchResultException e) {
                }
            }
            for (Song song : songs) {
                String lyrics = BugsCrawler.getLyrics(song.getBugsId());
                song.setLyric(lyrics.toCharArray());
                em.persist(song);
                List<WordInfo> wordInfos = wordExtractor.extractWords(song);
                if (wordInfos == null)
                    continue;
                wordExtractor.findMeaning(wordInfos, song);

                for (WordInfo wordInfo : wordInfos) {
                    String spelling = wordInfo.getLemma();
                    Word findWord = wordRepository.findBySpelling(spelling);
                    if (findWord == null && !wordInfo.getMeaning().isEmpty()) {
                        List<String> before = wordInfo.getMeaning();
                        before = before.stream()
                                .map(s -> s.endsWith(".") ? s.substring(0, s.length() - 1) : s)
                                .filter(s -> !s.isEmpty() && s.chars().allMatch(ch -> ch < '\u4E00' || ch > '\u9FFF'))//이거 빼도 됨
                                .toList();
                        if (before.isEmpty())
                            continue;
                        List<Meaning> after = before.stream().map(Meaning::new).toList();
                        Word newWord = new Word(
                                wordInfo.getLemma(),
                                wordInfo.getPronunciation(),
                                after,
                                wordInfo.getSpeechFields());
                        em.persist(newWord);
                        for (Meaning meaning : after) {
                            meaning.setWord(newWord);
                        }
                        SongWord songWord = new SongWord();
                        songWord.createSongWord(song, newWord, wordInfo.getSurface());
                        song.getSongWords().add(songWord);
                    } else {
                        if (findWord != null) {
                            SongWord songWord = new SongWord();
                            songWord.createSongWord(song, findWord, wordInfo.getSurface());
                            song.getSongWords().add(songWord);
                        }
                    }
                }
            }

            for (int i = 0; i < songs.size(); i++)
                new MemberSong().createMemberSong(members[0], songs.get(i));

            for (Song song : songs) {
                quizService.createMeaningQuiz(song.getId());
                quizService.createReadingQuiz(song.getId());
                quizService.createSentenceQuiz(song.getId());
            }
        }
    }

    @Getter
    @Setter
    static class Inputs {
        String artist, title;

        public Inputs(String artist, String title) {
            this.artist = artist;
            this.title = title;
        }

        public Inputs() {
        }
    }
}
