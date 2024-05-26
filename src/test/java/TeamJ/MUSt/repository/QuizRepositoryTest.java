package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Quiz;
import TeamJ.MUSt.domain.QuizType;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.repository.song.SongRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static TeamJ.MUSt.domain.QuizType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class QuizRepositoryTest {
    @Autowired
    QuizRepository quizRepository;

    @Autowired
    SongRepository songRepository;

    @Test
    public void 퀴즈_있는지_확인하기() throws Exception{
        Song savedSong = songRepository.save(new Song());
        Quiz savedQuiz = quizRepository.save(new Quiz(savedSong, null, MEANING, null, null));
        boolean exists = quizRepository.existsBySongIdAndType(savedSong.getId(), MEANING);
        assertThat(exists).isTrue();
    }

    @Test
    public void 노래_id와_퀴즈_타입으로_조회() throws Exception{
        Song savedSong = songRepository.save(new Song());
        Quiz quiz1 = quizRepository.save(new Quiz(savedSong, null, MEANING, null, null));
        Quiz quiz2 = quizRepository.save(new Quiz(savedSong, null, MEANING, null, null));
        //List<Quiz> findQuiz = quizRepository.findBySongIdAndType(savedSong.getId(), MEANING, PageRequest.of(0, 20));
        //assertThat(findQuiz.size()).isEqualTo(2);
        //assertThat(findQuiz.get(0)).isEqualTo(quiz1);
        //assertThat(findQuiz.get(1)).isEqualTo(quiz2);
    }
}