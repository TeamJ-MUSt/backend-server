package TeamJ.MUSt.repository.meaning;

import TeamJ.MUSt.domain.Meaning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeaningRepository extends JpaRepository<Meaning, Long>, MeaningRepositoryCustom {
    List<Meaning> findByWordIdIn(List<Long> ids);
}
