package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Quiz;
import TeamJ.MUSt.domain.QuizType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Page<Quiz> findBySongIdAndType(Long songId, QuizType type, Pageable page);

    Quiz findFirst1BySongIdAndType(Long songId, QuizType type);

    boolean existsBySongIdAndType(Long songId, QuizType type);

    int countBySongIdAndType(Long songId, QuizType type);
}
