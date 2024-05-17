package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Quiz;
import TeamJ.MUSt.domain.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository  extends JpaRepository<Quiz, Long> {
    List<Quiz> findBySongIdAndType(Long songId, QuizType type);

    Quiz findFirst1BySongIdAndType(Long songId, QuizType type);

    boolean existsBySongIdAndType(Long songId, QuizType type);
}
