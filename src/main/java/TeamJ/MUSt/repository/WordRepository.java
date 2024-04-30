package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByMemberId(Long memberId);
}
