package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.WordBook;
import TeamJ.MUSt.repository.wordbook.WordBookQueryDto;
import TeamJ.MUSt.service.WordBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WordBookController {
    private final WordBookService wordBookService;

    @GetMapping("/word-book/{userId}")
    public List<WordBookQueryDto> words(@PathVariable Long userId){
        List<WordBook> userWord = wordBookService.findUserWord(userId);
        return userWord.stream().map(wordBook -> new WordBookQueryDto(wordBook)).toList();
    }

    @PostMapping("/word-book/{userId}/delete")
    public void delete(@ModelAttribute WordSearch wordSearch){
        wordBookService.deleteWord(wordSearch);
    }
}
