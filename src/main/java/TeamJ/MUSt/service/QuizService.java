package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.*;
import TeamJ.MUSt.repository.AnswerRepository;
import TeamJ.MUSt.repository.ChoiceRepository;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.quiz.QuizRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.repository.songword.SongWordRepository;
import TeamJ.MUSt.repository.word.WordRepository;
import TeamJ.MUSt.repository.wordbook.MemberWordRepository;
import TeamJ.MUSt.util.NlpModule;
import TeamJ.MUSt.util.SentenceSplitter;
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
    private static final Random random = new Random();
    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;
    private final SongRepository songRepository;
    private final MemberWordRepository wordBookRepository;
    private final WordRepository wordRepository;
    private final SongWordRepository songWordRepository;
    private final SentenceSplitter splitter;
    private final NlpModule module;
    private final AnswerRepository answerRepository;
    private final ChoiceRepository choiceRepository;

    public List<Quiz> findQuizzes(Long songId, QuizType type, Integer pageNum) {
        //Page<Quiz> page = quizRepository.findBySongIdAndType(songId, type, PageRequest.of(pageNum, 20));
        //List<Quiz> result = new ArrayList<>(page.getContent());
        Page<Quiz> page = quizRepository.findQuizSet(songId, type, PageRequest.of(pageNum, 20));
        List<Quiz> result = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = page.getTotalPages();
        if (pageNum == totalPages - 2 && totalElements - 20L * (pageNum + 1) <= 10) {
            //Page<Quiz> lastPage = quizRepository.findBySongIdAndType(songId, type, PageRequest.of(totalPages - 1, 20));
            Page<Quiz> lastPage = quizRepository.findQuizSet(songId, type, PageRequest.of(totalPages - 1, 20));
            result.addAll(lastPage.getContent());
        }
        return result;
    }

    @Transactional
    public void updateWordBook(Long userId, Long songId) {
        Optional<Member> findMember = memberRepository.findById(userId);
        List<Word> usedWords = songRepository.findUsedWords(songId);
        for (Word word : usedWords) {
            MemberWord wordBook = new MemberWord(findMember.get(), word);
            wordBookRepository.save(wordBook);
        }
    }
    
    @Transactional
    public List<Quiz> createMeaningQuiz(Long songId) throws IOException {
        StringBuilder queries = new StringBuilder();
        ArrayList<Quiz> newQuizList = new ArrayList<>();
        ArrayList<Choice> newChoiceList = new ArrayList<>();
        ArrayList<Answer> newAnswerList = new ArrayList<>();
        if (hasQuizAlready(songId, MEANING))
            return null;
        Song targetSong = songRepository.findById(songId).get();
        List<SongWord> songWords = targetSong.getSongWords();

        HashMap<String, List<Word>> wordMapByField = new HashMap<>();

        for (SongWord songWord : songWords) {
            Word targetWord = songWord.getWord();
            String conjugation = songWord.getSurface();
            List<Word> words;
            String classOfWord = targetWord.getClassOfWord();
            if(wordMapByField.containsKey(classOfWord))
                words = wordMapByField.get(classOfWord);
            else{
                words = wordRepository.findByClassOfWord(classOfWord);
                wordMapByField.put(classOfWord, words);
            }
            long count = words.size();
            if(count < 3)
                continue;
            String querySentence = module.createQuerySentence(new String(targetSong.getLyric()), targetWord, conjugation);
            queries.append(querySentence).append("@@");
            long[] randomIds = new long[3];
            createRandomIds(count, randomIds);


            Quiz newQuiz = new Quiz(targetSong, targetWord, MEANING);

            List<Choice> createdChoices = createMeaningChoices(randomIds, newQuiz, words);
            newChoiceList.addAll(createdChoices);
            newQuizList.add(newQuiz);
        }
        String entireQuery = queries.toString();
        if (entireQuery.isEmpty())
            return newQuizList;
        ArrayList<String> usedMeaningIndex = module.reflectContext(entireQuery.substring(0, entireQuery.length() - 2));
        for (int i = 0; i < newQuizList.size(); i++) {
            Answer createdAnswer = createMeaningAnswer(newQuizList.get(i).getWord(), newQuizList.get(i), Integer.parseInt(usedMeaningIndex.get(i)));
            newAnswerList.add(createdAnswer);
        };

        quizRepository.bulkSaveQuiz(newQuizList);

        choiceRepository.bulkSaveChoice(newChoiceList);

        answerRepository.bulkSaveAnswer(newAnswerList);
        return newQuizList;
    }
    @Transactional
    public List<Quiz> createReadingQuiz(Long songId) {
        if (hasQuizAlready(songId, READING))
            return null;

        ArrayList<Quiz> newQuizList = new ArrayList<>();
        ArrayList<Choice> newChoiceList = new ArrayList<>();
        ArrayList<Answer> newAnswerList = new ArrayList<>();

        Song targetSong = songRepository.findById(songId).get();
        List<SongWord> songWords = songWordRepository.findSongWordsWithWordBySongId(songId);
        List<Word> choiceWords = wordRepository.findWithoutHiragana();
        for (SongWord songWord : songWords) {
            Word targetWord = songWord.getWord();

            long count = choiceWords.size();

            long[] randomIds = new long[3];
            createRandomIds(count, randomIds);

            Quiz newQuiz = new Quiz(targetSong, targetWord, READING);
            newQuizList.add(newQuiz);

            List<Choice> createdChoices = createReadingChoices(randomIds, newQuiz, choiceWords);
            newChoiceList.addAll(createdChoices);
            Answer newAnswer = createReadingAnswer(targetWord, newQuiz);
            newAnswerList.add(newAnswer);
        }
        quizRepository.bulkSaveQuiz(newQuizList);
        choiceRepository.bulkSaveChoice(newChoiceList);
        answerRepository.bulkSaveAnswer(newAnswerList);
        return newQuizList;
    }

    @Transactional
    public List<Quiz> createSentenceQuiz(Long songId) throws IOException {
        if (hasQuizAlready(songId, SENTENCE))
            return null;

        Song targetSong = songRepository.findById(songId).get();
        String[] sentences = new String(targetSong.getLyric()).split("\\\\n");
        ArrayList<Quiz> newQuizList = new ArrayList<>();
        ArrayList<Choice> newChoiceList = new ArrayList<>();
        ArrayList<Answer> newAnswerList = new ArrayList<>();

        for (String sentence : sentences) {
            String inputSentence = sentence.replaceAll(" ", "");
            String[] segments = splitter.splitSentence(inputSentence);
            if (segments == null)
                continue;
            segments = Arrays.stream(segments)
                    .filter(s -> !(s.equals("\\u200b") || s.equals("\\u3000b")))
                    .map(s -> s.replace("\\u200b", "").replace("\\u3000b", ""))
                    .toArray(String[]::new);
            if (segments.length > 6 || segments.length < 2)
                continue;
            Quiz newQuiz = new Quiz(targetSong, null, SENTENCE);
            newQuizList.add(newQuiz);

            Answer createdAnswer = createSentenceAnswer(sentence, newQuiz);
            newAnswerList.add(createdAnswer);
            List<Choice> createdChoices = createSentenceChoices(newQuiz, segments);
            newChoiceList.addAll(createdChoices);
        }

        quizRepository.bulkSaveQuiz(newQuizList);
        choiceRepository.bulkSaveChoice(newChoiceList);
        answerRepository.bulkSaveAnswer(newAnswerList);
        return newQuizList;
    }

    private boolean hasQuizAlready(Long songId, QuizType type) {
        return quizRepository.existsBySongIdAndType(songId, type);
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

    private List<Choice> createMeaningChoices(long[] randomIds, Quiz quiz, List<Word> words) {
        List<Choice> createdChoices = new ArrayList<>();
        for (long randomId : randomIds) {
            Meaning findMeaning = words.get((int) randomId).getMeaning().get(0);
            Choice choice = new Choice(findMeaning.getMeaning().trim(), quiz);
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
        createdAnswer = new Answer(targetWord.getMeaning().get(order).getMeaning(), quiz);
        return createdAnswer;
    }
    private Answer createReadingAnswer(Word targetWord, Quiz quiz) {
        return new Answer(targetWord.getJpPronunciation(), quiz);
    }

    private Answer createSentenceAnswer(String sentence, Quiz quiz) {
        return new Answer(sentence.replace(" ", ""), quiz);
    }

    private void createRandomIds(long count, long[] randomIds) {
        Set<Long> uniqueIds = new HashSet<>();
        while (uniqueIds.size() < randomIds.length) {
            long num = random.nextLong(count);
            uniqueIds.add(num);
        }

        int index = 0;
        for (Long id : uniqueIds) {
            randomIds[index++] = id;
        }
    }
}
