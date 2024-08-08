package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.*;
import TeamJ.MUSt.repository.AnswerRepository;
import TeamJ.MUSt.repository.ChoiceRepository;
import TeamJ.MUSt.repository.quiz.QuizRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.repository.songword.SongWordRepository;
import TeamJ.MUSt.repository.word.WordRepository;
import TeamJ.MUSt.util.NlpModule;
import TeamJ.MUSt.util.SentenceSplitter;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static TeamJ.MUSt.domain.QuizType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {
    public static final int PAGE_SIZE = 20;
    public static final StringBuilder QUERY_STRING_BUILDER = new StringBuilder();
    public static final String QUERY_DELIMITER = "@@";
    private final Random RANDOM_GENERATOR = new Random();

    private final QuizRepository quizRepository;
    private final SongRepository songRepository;
    private final WordRepository wordRepository;
    private final SongWordRepository songWordRepository;
    private final AnswerRepository answerRepository;
    private final ChoiceRepository choiceRepository;

    public List<Quiz> findQuizzes(Long songId, QuizType type, Integer pageNum) {
        //Page<Quiz> page = quizRepository.findBySongIdAndType(songId, type, PageRequest.of(pageNum, 20));
        //List<Quiz> result = new ArrayList<>(page.getContent());
        Page<Quiz> page = quizRepository.findQuizSet(songId, type, PageRequest.of(pageNum, PAGE_SIZE));

        List<Quiz> result = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = page.getTotalPages();

        if (doesNeedToMergeLastPage(pageNum, totalPages, totalElements)) {
            //Page<Quiz> lastPage = quizRepository.findBySongIdAndType(songId, type, PageRequest.of(totalPages - 1, 20));
            Page<Quiz> lastPage = quizRepository.findQuizSet(songId, type, PageRequest.of(totalPages - 1, PAGE_SIZE));
            mergeLastTwoPage(result, lastPage);
        }

        return result;
    }
    @Transactional
    public List<Quiz> createMeaningQuiz(Long songId) throws IOException {

        if (doesSongAlreadyHaveQuizTypeOf(songId, MEANING))
            return new ArrayList<>();

        Song targetSong = songRepository.findById(songId).get();
        List<SongWord> wordsInTargetSong = targetSong.getSongWords();

        ArrayList<Quiz> newQuizList = new ArrayList<>();
        ArrayList<Choice> newChoiceList = new ArrayList<>();
        ArrayList<Answer> newAnswerList = new ArrayList<>();
        
        HashMap<String, List<Word>> wordsMapByClass = new HashMap<>();

        for (SongWord wordInTargetSong : wordsInTargetSong) {
            Word targetWord = wordInTargetSong.getWord();
            String conjugation = wordInTargetSong.getSurface();

            List<Word> wordsGroupOfSameClass = getWordsGroupOfSameClass(targetWord, wordsMapByClass);

            long count = wordsGroupOfSameClass.size();

            if(canMakeThreeChoices(count)){
                createQuerySentence(targetSong, targetWord, conjugation);

                long[] chosenIds = new long[3];
                chooseThreeIdRandomly(count, chosenIds);

                Quiz newQuiz = new Quiz(targetSong, targetWord, MEANING);
                newQuizList.add(newQuiz);

                List<Choice> createdChoices = createMeaningChoices(chosenIds, newQuiz, wordsGroupOfSameClass);
                newChoiceList.addAll(createdChoices);
            }
        }

        String entireQuery = QUERY_STRING_BUILDER.toString();
        entireQuery = entireQuery.substring(0, entireQuery.length() - 2);

        if (canMakeMeaningQuiz(entireQuery)){
            createAnswerReflectingContext(entireQuery, newQuizList, newAnswerList);

            quizRepository.bulkSave(newQuizList);
            choiceRepository.bulkSaveChoice(newChoiceList);
            answerRepository.bulkSaveAnswer(newAnswerList);

            return newQuizList;
        }

        return newQuizList;
    }

    @Transactional
    public List<Quiz> createReadingQuiz(Long songId) {
        if (doesSongAlreadyHaveQuizTypeOf(songId, READING))
            return new ArrayList<>();

        ArrayList<Quiz> newQuizList = new ArrayList<>();
        ArrayList<Choice> newChoiceList = new ArrayList<>();
        ArrayList<Answer> newAnswerList = new ArrayList<>();

        Song targetSong = songRepository.findById(songId).get();
        List<SongWord> wordsInTargetSong = songWordRepository.findWithWordBySongId(songId);
        List<Word> candidateChoiceWords = wordRepository.findWithoutHiragana();
        
        for (SongWord wordInTargetSong : wordsInTargetSong) {
            Word targetWord = wordInTargetSong.getWord();

            long count = candidateChoiceWords.size();

            long[] chosenIds = new long[3];
            chooseThreeIdRandomly(count, chosenIds);

            Quiz newQuiz = new Quiz(targetSong, targetWord, READING);
            newQuizList.add(newQuiz);
            
            List<Choice> newChoices = createReadingChoices(chosenIds, newQuiz, candidateChoiceWords);
            newChoiceList.addAll(newChoices);
            
            
            Answer newAnswer = createReadingAnswer(targetWord, newQuiz);
            newAnswerList.add(newAnswer);
        }
        
        quizRepository.bulkSave(newQuizList);
        choiceRepository.bulkSaveChoice(newChoiceList);
        answerRepository.bulkSaveAnswer(newAnswerList);
        
        return newQuizList;
    }

    @Transactional
    public List<Quiz> createSentenceQuiz(Long songId) throws IOException {
        if (doesSongAlreadyHaveQuizTypeOf(songId, SENTENCE))
            return new ArrayList<>();

        Song targetSong = songRepository.findById(songId).get();
        String[] sentences = convertLyricLobToString(targetSong).split("\\\\n");
        
        ArrayList<Quiz> newQuizList = new ArrayList<>();
        ArrayList<Choice> newChoiceList = new ArrayList<>();
        ArrayList<Answer> newAnswerList = new ArrayList<>();

        for (String sentence : sentences) {
            String[] segments = splitIntoSegments(sentence);

            if (segments.length == 0)
                continue;

            segments = Arrays.stream(segments)
                    .filter(s -> !(s.equals("\\u200b") || s.equals("\\u3000b")))
                    .map(s -> s.replace("\\u200b", "").replace("\\u3000b", ""))
                    .toArray(String[]::new);

            if (isSegmentsTooMany(segments))
                continue;

            Quiz newQuiz = new Quiz(targetSong, null, SENTENCE);
            newQuizList.add(newQuiz);

            List<Choice> newChoices = createSentenceChoices(newQuiz, segments);
            newChoiceList.addAll(newChoices);

            Answer createdAnswer = createSentenceAnswer(sentence, newQuiz);
            newAnswerList.add(createdAnswer);
        }

        quizRepository.bulkSave(newQuizList);
        choiceRepository.bulkSaveChoice(newChoiceList);
        answerRepository.bulkSaveAnswer(newAnswerList);

        return newQuizList;
    }
    private List<Choice> createReadingChoices(long[] randomIds, Quiz quiz, List<Word> words) {
        List<Choice> createdChoices = new ArrayList<>();
        for (long randomId : randomIds) {
            Word selected = words.get((int) randomId);
            Choice choice = new Choice(selected.getJpPronunciation(), quiz);
            createdChoices.add(choice);
        }
        return createdChoices;
    }

    private List<Choice> createMeaningChoices(long[] chosenIds, Quiz quiz, List<Word> words) {
        List<Choice> createdChoices = new ArrayList<>();
        for (long randomId : chosenIds) {
            Meaning findMeaning = words.get((int) randomId).getMeaning().get(0);
            Choice choice = new Choice(findMeaning.getContent().trim(), quiz);
            createdChoices.add(choice);
        }
        return createdChoices;
    }

    private List<Choice> createSentenceChoices(Quiz quiz, String[] segments) {
        String[] shuffled = new String[segments.length];
        List<String> list = Arrays.asList(segments);

        Collections.shuffle(list);
        list.toArray(shuffled);
        return Arrays.stream(shuffled).map(s -> new Choice(s, quiz)).toList();
    }

    private Answer createMeaningAnswer(Word targetWord, Quiz quiz, int order) {
        Answer createdAnswer;
        createdAnswer = new Answer(targetWord.getMeaning().get(order).getContent(), quiz);
        return createdAnswer;
    }
    private Answer createReadingAnswer(Word targetWord, Quiz quiz) {
        return new Answer(targetWord.getJpPronunciation(), quiz);
    }

    private Answer createSentenceAnswer(String sentence, Quiz quiz) {
        return new Answer(sentence.replace(" ", ""), quiz);
    }

    private void chooseThreeIdRandomly(long count, long[] randomIds) {
        Set<Long> uniqueIds = new HashSet<>();
        while (uniqueIds.size() < randomIds.length) {
            long num = RANDOM_GENERATOR.nextLong(count);
            uniqueIds.add(num);
        }

        int index = 0;
        for (Long id : uniqueIds) {
            randomIds[index++] = id;
        }
    }
    private List<Word> getWordsGroupOfSameClass(Word targetWord, HashMap<String, List<Word>> wordsMapByClass) {
        List<Word> wordsGroupOfSameClass;
        String classOfWord = targetWord.getClassOfWord();

        if(hasAlreadyFindWordOfClass(classOfWord, wordsMapByClass))
            wordsGroupOfSameClass = wordsMapByClass.get(classOfWord);
        else{
            wordsGroupOfSameClass = wordRepository.findByClassOfWord(classOfWord);
            wordsMapByClass.put(classOfWord, wordsGroupOfSameClass);
        }

        return wordsGroupOfSameClass;
    }

    private void createAnswerReflectingContext(String entireQuery, ArrayList<Quiz> newQuizList, ArrayList<Answer> newAnswerList) throws IOException {
        ArrayList<String> usedMeaningIndexList = NlpModule.reflectContext(entireQuery);

        for (int i = 0; i < newQuizList.size(); i++) {
            Quiz newQuiz = newQuizList.get(i);
            int usedMeaningIndex = Integer.parseInt(usedMeaningIndexList.get(i));
            Answer newAnswer = createMeaningAnswer(newQuiz.getWord(), newQuiz, usedMeaningIndex);
            newAnswerList.add(newAnswer);
        }
    }
    private void createQuerySentence(Song targetSong, Word targetWord, String conjugation) throws JsonProcessingException {
        String querySentence = NlpModule.createQuerySentence(convertLyricLobToString(targetSong), targetWord, conjugation);
        QUERY_STRING_BUILDER.append(querySentence).append(QUERY_DELIMITER);
    }
    private String[] splitIntoSegments(String sentence) throws IOException {
        String inputSentence = removeSpace(sentence);
        return SentenceSplitter.splitSentence(inputSentence);
    }

    private boolean doesSongAlreadyHaveQuizTypeOf(Long songId, QuizType type) {
        return quizRepository.existsBySongIdAndType(songId, type);
    }
    private static void mergeLastTwoPage(List<Quiz> result, Page<Quiz> lastPage) {
        result.addAll(lastPage.getContent());
    }

    private static boolean doesNeedToMergeLastPage(Integer pageNum, int totalPages, long totalElements) {
        return isLastPage(pageNum, totalPages) && isLastPageQuizCountTenOrLess(pageNum, totalElements);
    }

    private static boolean isLastPageQuizCountTenOrLess(Integer pageNum, long totalElements) {
        return totalElements - 20L * (pageNum + 1) <= 10;
    }

    private static boolean isLastPage(Integer pageNum, int totalPages) {
        return pageNum == totalPages - 2;
    }

    private static boolean canMakeMeaningQuiz(String entireQuery) {
        if(entireQuery.isEmpty())
            return false;

        return true;
    }

    private static String convertLyricLobToString(Song targetSong) {
        return new String(targetSong.getLyric());
    }

    private static boolean canMakeThreeChoices(long count) {
        return count >= 3;
    }

    private static boolean hasAlreadyFindWordOfClass(String classOfWord, HashMap<String, List<Word>> wordsMapByClass) {
        return wordsMapByClass.containsKey(classOfWord);
    }

    private static String removeSpace(String sentence) {
        return sentence.replaceAll(" ", "");
    }

    private static boolean isSegmentsTooMany(String[] segments) {
        return segments.length > 6 || segments.length < 2;
    }
}
