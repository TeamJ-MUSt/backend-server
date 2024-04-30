package TeamJ.MUSt.service;

import TeamJ.MUSt.controller.WordSearch;
import TeamJ.MUSt.domain.WordBook;
import TeamJ.MUSt.repository.wordbook.WordBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordBookService {
    private final WordBookRepository wordBookRepository;

    public List<WordBook> findUserWord(Long userId){

        return wordBookRepository.findWithMemberAndWord(userId);

    }

    public void deleteWord(WordSearch wordSearch) {
        wordBookRepository.deleteBySpellingAndMeaning(wordSearch.getSpell(), wordSearch.getMeaning());
    }
}
