package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository  extends JpaRepository<Quiz, Long> {
    List<Quiz> findBySongIdAndType(Long songId, String type);
}
