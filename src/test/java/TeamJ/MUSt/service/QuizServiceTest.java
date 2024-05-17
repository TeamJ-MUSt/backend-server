package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.*;
import TeamJ.MUSt.repository.QuizRepository;
import TeamJ.MUSt.repository.WordRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static TeamJ.MUSt.domain.QuizType.*;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest
@Transactional
class QuizServiceTest {
    @Autowired
    QuizService quizService;
    @Autowired
    WordRepository wordRepository;
    @Autowired
    QuizRepository quizRepository;
    @Test
    public void 뜻_퀴즈_생성() throws Exception{
        List<Quiz> meaningQuiz = quizService.createMeaningQuiz(1l);
        List<Word> wordsWithSong = wordRepository.findWithSong(1l);
        assertThat(meaningQuiz.size()).isEqualTo(wordsWithSong.size());
        for(int i = 0; i < meaningQuiz.size(); i++){
            Quiz quiz = meaningQuiz.get(i);
            Word word = wordsWithSong.get(i);
            assertThat(quiz.getWord().getSpelling()).isEqualTo(word.getSpelling().trim());
            assertThat(quiz.getType()).isEqualTo(MEANING);
        }
    }

    @Test
    public void 발음_퀴즈_생성() throws Exception{
        List<Quiz> readingQuiz = quizService.createReadingQuiz(1l);
        List<Word> wordsWithSong = wordRepository.findWithSong(1l);
        assertThat(readingQuiz.size()).isEqualTo(wordsWithSong.size());
        for(int i = 0; i < readingQuiz.size(); i++){
            Quiz quiz = readingQuiz.get(i);
            Word word = wordsWithSong.get(i);
            assertThat(quiz.getWord().getJpPronunciation()).isEqualTo(word.getJpPronunciation());
            assertThat(quiz.getType()).isEqualTo(READING);
        }
    }

    @Test
    public void 유저가_뜻_퀴즈_생성_후_요청() throws Exception{
        List<Quiz> meaningQuiz = quizService.createMeaningQuiz(1l);
        List<Quiz> quizzes = quizService.findQuizzes(1l, MEANING);
        assertThat(meaningQuiz.size()).isEqualTo(quizzes.size());
    }
    @Test
    public void 유저가_발음_퀴즈_생성_후_요청() throws Exception{
        List<Quiz> readingQuiz = quizService.createReadingQuiz(1l);
        List<Quiz> quizzes = quizService.findQuizzes(1l, READING);
        assertThat(readingQuiz.size()).isEqualTo(quizzes.size());
    }

}