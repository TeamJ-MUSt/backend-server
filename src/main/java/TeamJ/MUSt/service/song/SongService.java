package TeamJ.MUSt.service.song;

import TeamJ.MUSt.domain.*;
import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.meaning.MeaningRepositoryImpl;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.repository.songword.SongWordRepository;
import TeamJ.MUSt.repository.word.WordRepository;
import TeamJ.MUSt.service.QuizService;
import TeamJ.MUSt.util.BugsCrawler;
import TeamJ.MUSt.util.WordExtractor;
import TeamJ.MUSt.util.WordInfo;
import com.querydsl.core.Tuple;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
@Slf4j
public class SongService {
    public static final String UNNECESSARY_PART_REGEX = "\\(.*?\\)";

    private final SongRepository songRepository;
    private final MemberRepository memberRepository;
    private final WordRepository wordRepository;
    private final QuizService quizService;
    private final SongWordRepository songWordRepository;
    private final MeaningRepositoryImpl meaningRepository;

    public List<Song> findSongOfMember(Long memberId) {
        return songRepository.findWithMemberSong(memberId);
    }

    public List<Tuple> searchSongWithCheckingRegisterStatus(Long memberId, String title, String artist) {
        return songRepository.findWithMemberSongCheckingRegister(memberId, title, artist);
    }

    @Transactional
    public List<Song> searchSongRemotely(String title, String artist) throws NoSearchResultException {
        List<Song> newSongs = new ArrayList<>();
        List<SongInfo> searchResults = BugsCrawler.searchSongsInBugs(title, artist);

        addNewSongsIntoList(searchResults, newSongs);

        return newSongs;
    }

    @Transactional
    public boolean registerSong(Long userId, Long songId, String bugsId) throws IOException {
        Member member = memberRepository.findById(userId).get();
        Song newSong = songRepository.findById(songId).get();

        if (doesAlreadyRegistered(newSong))
            return false;

        String lyric = BugsCrawler.getLyrics(bugsId);
        if (hasLyric(lyric))
            lyric = trimLyric(lyric);
        else
            lyric = "";
        newSong.updateLyric(convertToClob(lyric));

        List<WordInfo> extractedWordInfos = WordExtractor.extractWords(newSong);

        if (extractedWordInfos == null || extractedWordInfos.isEmpty())
            return false;

        List<WordInfo> newWordsInfos = new ArrayList<>();
        checkIfExtractedWordIsNewWord(extractedWordInfos, newWordsInfos, newSong);

        WordExtractor.findMeaning(newWordsInfos, newSong);

        List<Word> newWords = new ArrayList<>();
        List<Meaning> newMeanings = new ArrayList<>();
        List<SongWord> newSongWords = new ArrayList<>();
        addEntities(newWordsInfos, newWords, newMeanings, newSongWords, newSong);

        wordRepository.bulkSave(newWords);
        meaningRepository.bulkSave(newMeanings);
        songWordRepository.bulkSave(newSongWords);

        MemberSong memberSong = new MemberSong();
        memberSong.createMemberSong(member, newSong);

        quizService.createMeaningQuiz(newSong.getId());
        quizService.createReadingQuiz(newSong.getId());
        quizService.createSentenceQuiz(newSong.getId());

        return true;
    }

    private void addNewSongsIntoList(List<SongInfo> searchResults, List<Song> newSongs) {
        for (SongInfo searchResult : searchResults) {
            if (hasSongAlreadyInDb(searchResult))
                continue;

            String titleOfSearchedSong = searchResult.getTitle();
            String artistOfSearchedSong = trimArtistName(searchResult.getArtist());

            Song newSong = new Song(
                    titleOfSearchedSong,
                    artistOfSearchedSong,
                    BugsCrawler.imageToByte(searchResult.getThumbnailUrl()),
                    BugsCrawler.imageToByte(searchResult.getThumbnailUrl()),
                    searchResult.getMusic_id()
            );

            songRepository.save(newSong);

            newSongs.add(newSong);
        }
    }

    private static void addEntities(
            List<WordInfo> newWordsInfos,
            List<Word> newWordList,
            List<Meaning> newMeaningList,
            List<SongWord> newSongWordList,
            Song newSong
    ) {

        for (WordInfo newWordInfo : newWordsInfos) {
            List<String> meaningsBeforeRefined = refineMeanings(newWordInfo);

            if (doesRemovedAllMeanings(meaningsBeforeRefined))
                continue;

            List<Meaning> meaningsAfterRefined = meaningsBeforeRefined
                    .stream()
                    .map(Meaning::new)
                    .toList();

            Word newWord = new Word(
                    newWordInfo.getLemma(),
                    newWordInfo.getPronunciation(),
                    meaningsAfterRefined,
                    newWordInfo.getSpeechFields());

            newWordList.add(newWord);

            for (Meaning meaning : meaningsAfterRefined){
                meaning.updateWord(newWord);
                newMeaningList.add(meaning);
            }

            SongWord songWord = new SongWord();
            songWord.createSongWord(newSong, newWord, newWordInfo.getSurface());
            newSongWordList.add(songWord);
        }
    }

    private static List<String> refineMeanings(WordInfo newWordInfo) {
        List<String> meaningsBeforeRefined = newWordInfo.getMeaning();

        meaningsBeforeRefined = meaningsBeforeRefined
                .stream()
                .map(s -> getStringRemovedLastDot(s))
                .filter(s -> hasValidCharactersOnly(s))
                .toList();
        return meaningsBeforeRefined;
    }


    private void checkIfExtractedWordIsNewWord(List<WordInfo> extractedWordInfos, List<WordInfo> newWords, Song newSong) {
        for (WordInfo extractedWord : extractedWordInfos) {
            String spelling = extractedWord.getLemma();
            Word findWord = wordRepository.findBySpelling(spelling);
            if (isNewWord(findWord)) {
                newWords.add(extractedWord);
                continue;
            }

            SongWord newSongWord = new SongWord();
            newSongWord.createSongWord(newSong, findWord, extractedWord.getSurface());
        }
    }



    private static boolean doesAlreadyRegistered(Song newSong) {
        if(newSong.getSongWords().isEmpty())
            return false;

        return true;
    }

    private boolean hasSongAlreadyInDb(SongInfo searchResult) {
        String title = searchResult.getTitle();
        String artist = searchResult.getArtist();

        if(songRepository.findRequestSong(title, artist).isEmpty())
            return false;

        return true;
    }

    private static String trimLyric(String lyric) {
        lyric = lyric.substring(0, lyric.length() - 2);
        lyric = lyric.substring(2);
        return lyric;
    }
    private static boolean hasValidCharactersOnly(String s) {
        if(s.isEmpty())
            return false;

        return s.chars().allMatch(ch -> ch < '一' || ch > '\u9FFF');
    }

    private static String getStringRemovedLastDot(String s) {
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }
    private static boolean doesRemovedAllMeanings(List<String> meaningsBeforeRefined) {
        return meaningsBeforeRefined.isEmpty();
    }

    private String trimArtistName(String artistName){
        return artistName.replaceAll(UNNECESSARY_PART_REGEX, "");
    }

    private static boolean hasLyric(String lyric) {
        return lyric.length() != 4;
    }

    private static char[] convertToClob(String lyric) {
        return lyric.toCharArray();
    }

    private static boolean isNewWord(Word findWord) {
        return findWord == null;
    }
}