package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.*;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.QuizRepository;
import TeamJ.MUSt.repository.WordRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.repository.wordbook.MemberWordRepository;
import TeamJ.MUSt.util.NlpModule;
import TeamJ.MUSt.util.SentenceSplitter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static TeamJ.MUSt.domain.QuizType.*;

@Service
@RequiredArgsConstructor
public class QuizService {
    private static final Random random = new Random();
    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;
    private final SongRepository songRepository;
    private final MemberWordRepository wordBookRepository;
    private final WordRepository wordRepository;
    private final SentenceSplitter splitter;
    private final NlpModule module;

    private static void createMeaningAnswer(List<Answer> answerList, Word targetWord, Quiz quiz, int order) {
        answerList.add(new Answer(targetWord.getMeaning().get(order).getMeaning(), quiz));
    }

    private static void createReadingAnswer(List<Answer> answerList, Word targetWord, Quiz quiz) {
        answerList.add(new Answer(targetWord.getJpPronunciation(), quiz));
    }

    private static void createSentenceAnswer(List<Answer> answerList, String sentence, Quiz quiz) {
        answerList.add(new Answer(sentence.replace(" ", ""), quiz));
    }

    private static void createRandomIds(long count, long[] randomIds) {
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

    public List<Quiz> findQuizzes(Long songId, QuizType type, Integer pageNum) {
        Page<Quiz> page = quizRepository.findBySongIdAndType(songId, type, PageRequest.of(pageNum, 20));
        List<Quiz> result = new ArrayList<>(page.getContent());
        long totalElements = page.getTotalElements();
        int totalPages = page.getTotalPages();
        if (pageNum == totalPages - 2 && totalElements - 20L * (pageNum + 1) <= 10) {
            Page<Quiz> lastPage = quizRepository.findBySongIdAndType(songId, type, PageRequest.of(totalPages - 1, 20));
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
        ArrayList<Quiz> quizList = new ArrayList<>();
        List<Choice> choiceList = new ArrayList<>();
        List<Answer> answerList = new ArrayList<>();

        if (hasQuizAlready(songId, MEANING))
            return null;

        Song targetSong = songRepository.findById(songId).get();
        List<SongWord> songWords = targetSong.getSongWords();
        List<Quiz> createdQuiz = new ArrayList<>();

        HashMap<String, Integer> freqTable = new HashMap<>();

        for (SongWord songWord : songWords)
            freqTable.put(songWord.getSurface(), 0);

        for (SongWord songWord : songWords) {
            Word targetWord = songWord.getWord();
            String conjugation = songWord.getSurface();
            List<Word> words = wordRepository.findByClassOfWord(targetWord.getClassOfWord());
            long count = words.size();
            int order = freqTable.get(conjugation);
            if (order > 0)
                continue;
            String querySentence = module.createQuerySentence(new String(targetSong.getLyric()), targetWord, conjugation, order);
            queries.append(querySentence).append("@@");
            long[] randomIds = new long[3];
            createRandomIds(count, randomIds);


            Quiz newQuiz = new Quiz(targetSong, targetWord, MEANING, answerList, choiceList);

            createMeaningChoices(randomIds, choiceList, newQuiz, words);
            quizList.add(newQuiz);
        }
        String entireQuery = queries.toString();
        if (entireQuery.isEmpty())
            return createdQuiz;
        ArrayList<String> usedMeaningIndex = module.reflectContextV2(entireQuery.substring(0, entireQuery.length() - 2));
        for (int i = 0; i < quizList.size(); i++) {
            createMeaningAnswer(answerList, quizList.get(i).getWord(), quizList.get(i), Integer.parseInt(usedMeaningIndex.get(i)));
            freqTable.merge(quizList.get(i).getWord().getSpelling(), 1, Integer::sum);

            quizRepository.save(quizList.get(i));
            createdQuiz.add(quizList.get(i));
        }
        return createdQuiz;
    }

    @Transactional
    public List<Quiz> createReadingQuiz(Long songId) {
        if (hasQuizAlready(songId, READING))
            return null;

        Song targetSong = songRepository.findById(songId).get();
        List<SongWord> songWords = targetSong.getSongWords();
        List<Quiz> createdQuiz = new ArrayList<>();
        List<Word> choiceWords = wordRepository.findWithoutHiragana();
        for (SongWord songWord : songWords) {
            Word targetWord = songWord.getWord();

            long count = choiceWords.size();

            long[] randomIds = new long[3];
            createRandomIds(count, randomIds);
            List<Choice> choiceList = new ArrayList<>();
            List<Answer> answerList = new ArrayList<>();

            Quiz newQuiz = new Quiz(targetSong, targetWord, READING, answerList, choiceList);

            createReadingChoices(randomIds, choiceList, newQuiz, choiceWords);
            createReadingAnswer(answerList, targetWord, newQuiz);
            quizRepository.save(newQuiz);
            createdQuiz.add(newQuiz);
        }

        return createdQuiz;
    }

    @Transactional
    public List<Quiz> createSentenceQuiz(Long songId) throws IOException {
        if (hasQuizAlready(songId, SENTENCE))
            return null;

        Song targetSong = songRepository.findById(songId).get();
        String[] sentences = new String(targetSong.getLyric()).split("\\\\n");
        List<Quiz> createdQuiz = new ArrayList<>();

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
            List<Choice> choiceList = new ArrayList<>();
            List<Answer> answerList = new ArrayList<>();
            Quiz newQuiz = new Quiz(targetSong, null, SENTENCE, answerList, choiceList);

            createSentenceAnswer(answerList, sentence, newQuiz);
            createSentenceChoices(choiceList, newQuiz, segments);
            quizRepository.save(newQuiz);
            createdQuiz.add(newQuiz);
        }

        return createdQuiz;
    }

    private boolean hasQuizAlready(Long songId, QuizType type) {
        return quizRepository.existsBySongIdAndType(songId, type);
    }

    private void createReadingChoices(long[] randomIds, List<Choice> choiceList, Quiz quiz, List<Word> words) {
        for (long randomId : randomIds) {
            Word selected = words.get((int) randomId);
            Choice choice = new Choice(selected.getJpPronunciation(), quiz);
            choiceList.add(choice);
        }
    }

    private void createMeaningChoices(long[] randomIds, List<Choice> choiceList, Quiz quiz, List<Word> words) {
        for (long randomId : randomIds) {
            Meaning findMeaning = words.get((int) randomId).getMeaning().get(0);
            Choice choice = new Choice(findMeaning.getMeaning().trim(), quiz);
            choiceList.add(choice);
        }
    }

    private void createSentenceChoices(List<Choice> choiceList, Quiz quiz, String[] segments) {
        String[] shuffled = new String[segments.length];
        List<String> list = Arrays.asList(segments);

        Collections.shuffle(list);
        list.toArray(shuffled);
        choiceList.addAll(Arrays.stream(shuffled).map(s -> new Choice(s, quiz)).toList());
    }
}
