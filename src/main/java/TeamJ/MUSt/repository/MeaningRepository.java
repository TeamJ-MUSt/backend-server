package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Meaning;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeaningRepository extends JpaRepository<Meaning, Long> {
    Meaning findFirstByWordId(Long wordId);
}
