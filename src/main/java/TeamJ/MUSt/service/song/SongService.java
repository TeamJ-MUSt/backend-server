package TeamJ.MUSt.service.song;

import TeamJ.MUSt.domain.*;
import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.WordRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.service.QuizService;
import TeamJ.MUSt.util.BugsCrawler;
import TeamJ.MUSt.util.WordExtractor;
import TeamJ.MUSt.util.WordInfo;
import com.querydsl.core.Tuple;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final MemberRepository memberRepository;
    private final WordExtractor wordExtractor;
    private final WordRepository wordRepository;
    private final QuizService quizService;

    public List<Song> findUserSong(Long memberId) {
        return songRepository.findWithMemberSong(memberId);
    }

    public List<Song> findUserSong(Long memberId, Pageable pageRequest) {
        return songRepository.findWithMemberSong(memberId, pageRequest);
    }

    public List<Song> searchMySong(Long memberId, String title, String artist) {
        return songRepository.findInMySong(memberId, title, artist);
    }

    public List<Song> searchDbSong(String title, String artist) {
        return songRepository.findRequestSong(title, artist);
    }

    public List<Tuple> searchSong(Long memberId, String title, String artist) {
        return songRepository.findWithMemberSongCheckingRegister(memberId, title, artist);
    }

    @Transactional
    public List<Song> searchRemoteSong(String title, String artist) throws NoSearchResultException {
        List<Song> newSongs = new ArrayList<>();
        List<SongInfo> searchResults = BugsCrawler.callBugsApi(title, artist);
        for (SongInfo searchResult : searchResults) {
            if (songRepository.findRequestSong(searchResult.getTitle(), searchResult.getArtist()).isEmpty()) {
                Song newSong = new Song(searchResult.getTitle(),
                        searchResult.getArtist().replaceAll("\\(.*?\\)", ""),
                        BugsCrawler.imageToByte(searchResult.getThumbnailUrl()),
                        BugsCrawler.imageToByte(searchResult.getThumbnailUrl()),
                        searchResult.getMusic_id());

                songRepository.save(newSong);
                newSongs.add(newSong);
            }
        }
        return newSongs;
    }
    @Transactional
    public boolean registerSong(Long userId, Long songId, String bugsId) throws IOException {
        Member member = memberRepository.findById(userId).get();
        Song newSong = songRepository.findById(songId).get();
        String lyric = BugsCrawler.getLyrics(bugsId);
        if (lyric.length() == 4)
            lyric = "";
        else {
            lyric = lyric.substring(0, lyric.length() - 2);
            lyric = lyric.substring(2);
        }
        newSong.setLyric(lyric.toCharArray());
        if (newSong.getSongWords().isEmpty()) {
            List<WordInfo> wordInfos = wordExtractor.extractWords(newSong);
            if (wordInfos.isEmpty())
                return false;
            ArrayList<WordInfo> newWords = new ArrayList<>();
            for (WordInfo wordInfo : wordInfos) {
                String spelling = wordInfo.getLemma();
                Word findWord = wordRepository.findBySpelling(spelling);
                if (findWord == null && !wordInfo.getMeaning().isEmpty()) {
                    newWords.add(wordInfo);
                } else {
                    if (findWord != null) {
                        SongWord songWord = new SongWord();
                        songWord.createSongWord(newSong, findWord, wordInfo.getSurface());
                    }
                }
            }
            wordExtractor.findMeaning(newWords, newSong);
            for (WordInfo newWordInfo : newWords) {
                List<String> before = newWordInfo.getMeaning();
                before = before.stream()
                        .map(s -> s.endsWith(".") ? s.substring(0, s.length() - 1) : s)
                        .filter(s -> !s.isEmpty() && s.chars().allMatch(ch -> ch < '\u4E00' || ch > '\u9FFF'))
                        .toList();
                if (before.isEmpty())
                    continue;
                List<Meaning> after = before.stream().map(Meaning::new).toList();
                Word newWord = new Word(
                        newWordInfo.getLemma(),
                        newWordInfo.getPronunciation(),
                        after,
                        newWordInfo.getSpeechFields());
                wordRepository.save(newWord);
                for (Meaning meaning : after)
                    meaning.setWord(newWord);

                SongWord songWord = new SongWord();
                songWord.createSongWord(newSong, newWord, newWordInfo.getSurface());
            }
        }
        MemberSong memberSong = new MemberSong();
        memberSong.createMemberSong(member, newSong);
        quizService.createMeaningQuiz(newSong.getId());
        quizService.createReadingQuiz(newSong.getId());
        quizService.createSentenceQuiz(newSong.getId());
        return true;
    }
}
