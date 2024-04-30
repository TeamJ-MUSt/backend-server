package TeamJ.MUSt.repository.wordbook;

import TeamJ.MUSt.domain.WordBook;

import java.util.List;

public interface WordBookRepositoryCustom {
    List<WordBook> findWithMemberAndWord(Long MemberId);
    long deleteBySpellingAndMeaning(String spelling, String meaning);
}
