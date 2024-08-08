package TeamJ.MUSt.repository.word;

import TeamJ.MUSt.domain.Word;

import java.util.List;

public interface WordRepositoryCustom {
    List<Word> bulkSave(List<Word> words);
}
