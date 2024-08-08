package TeamJ.MUSt.repository.quiz;

import TeamJ.MUSt.domain.Quiz;
import TeamJ.MUSt.domain.QuizType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuizRepositoryCustom {
    Page<Quiz> findQuizSet(Long songId, QuizType type, Pageable page);

    void bulkSave(List<Quiz> quizzes);
}
