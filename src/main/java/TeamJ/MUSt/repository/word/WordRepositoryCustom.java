package TeamJ.MUSt.repository.word;

import TeamJ.MUSt.domain.Word;

import java.util.List;

public interface WordRepositoryCustom {
    List<Word> bulkSaveWord(List<Word> words);
}
