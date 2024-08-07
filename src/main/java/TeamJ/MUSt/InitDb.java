package TeamJ.MUSt;

import TeamJ.MUSt.domain.*;
import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.repository.meaning.MeaningRepositoryImpl;
import TeamJ.MUSt.repository.songword.SongWordRepository;
import TeamJ.MUSt.repository.word.WordRepository;
import TeamJ.MUSt.service.QuizService;
import TeamJ.MUSt.service.song.SongInfo;
import TeamJ.MUSt.util.BugsCrawler;
import TeamJ.MUSt.util.WordExtractor;
import TeamJ.MUSt.util.WordInfo;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    private static Song createSampleSong(String title, String artist) throws NoSearchResultException {
        List<SongInfo> songInfos = BugsCrawler.searchSongsInBugs(title, artist);
        SongInfo firstSong = songInfos.get(0);
        return new Song(
                title,
                artist,
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

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        private final WordRepository wordRepository;
        private final MeaningRepositoryImpl meaningRepository;
        private final SongWordRepository songWordRepository;
        private final QuizService quizService;

        public void initDb() throws IOException, NoSearchResultException {
            Member[] members = new Member[2];
            members[0] = new Member("member1", "0");
            members[1] = new Member("member2", "0");

            for (Member member : members)
                em.persist(member);

            /*Song[] songs = new Song[8];
            songs[0] = createSampleSong("BETELGEUSE", "Yuuri");
            songs[1] = createSampleSong("nandemonaiya", "RADWIMPS");
            songs[2] = createSampleSong("lemon", "Yonezu kenshi");
            songs[3] = createSampleSong("Flamingo", "Yonezu kenshi");
            songs[4] = createSampleSong("Madou Ito", "Masaki Suda");
            songs[5] = createSampleSong("Leo", "Yuuri");
            songs[6] = createSampleSong("BETELGEUSE", "KSUKE");
            songs[7] = createSampleSong("Bling-Bang-Bang-Born", "Creepy Nuts");
            List<Word> newWords = new ArrayList<>();
            List<Meaning> newMeanings = new ArrayList<>();
            List<SongWord> newSongWord = new ArrayList<>();

            HashMap<String, Word> existingWordSet = new HashMap<>();

            for (Song song : songs) {
                String lyrics = BugsCrawler.getLyrics(song.getBugsId());
                song.updateLyric(lyrics.toCharArray());
                em.persist(song);
                List<WordInfo> wordInfos = WordExtractor.extractWords(song);
                if (wordInfos == null)
                    continue;
                WordExtractor.findMeaning(wordInfos, song);

                for (WordInfo wordInfo : wordInfos) {
                    String spelling = wordInfo.getLemma();
                    Word findWord = existingWordSet.get(spelling);
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
                        newWords.add(newWord);
                        existingWordSet.put(spelling, newWord);
                        for (Meaning meaning : after) {
                            meaning.updateWord(newWord);
                            newMeanings.add(meaning);
                        }
                        SongWord songWord = new SongWord();
                        songWord.createSongWord(song, newWord, wordInfo.getSurface());
                        song.getSongWords().add(songWord);
                        newSongWord.add(songWord);
                    } else {
                        if (findWord != null) {
                            SongWord songWord = new SongWord();
                            songWord.createSongWord(song, findWord, wordInfo.getSurface());
                            song.getSongWords().add(songWord);
                            newSongWord.add(songWord);
                        }
                    }
                }
            }
            wordRepository.bulkSave(newWords);
            meaningRepository.bulkSave(newMeanings);
            songWordRepository.bulkSave(newSongWord);
            for (int i = 0; i < songs.length; i++)
                new MemberSong().createMemberSong(members[0], songs[i]);*/
        }
    }
}
