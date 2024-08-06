package TeamJ.MUSt.repository.meaning;

import TeamJ.MUSt.domain.Meaning;

import java.util.List;

public interface MeaningRepositoryCustom {
    void bulkSaveMeaning(List<Meaning> meanings);
}
