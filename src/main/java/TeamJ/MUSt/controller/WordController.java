package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.WordRepository;
import TeamJ.MUSt.util.AllFieldWordInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(maxAge = 3600)
@RequiredArgsConstructor
public class WordController {
    private final WordRepository wordRepository;

    @GetMapping("/words")
    public List<AllFieldWordInfo> allWord() throws IOException {
        List<AllFieldWordInfo> result = new ArrayList<>();
        List<Word> withMeaning = wordRepository.findWithMeaning();
        for (Word word : withMeaning) {
            AllFieldWordInfo item = new AllFieldWordInfo(word.getClassOfWord(), word.getJpPronunciation(), word.getSpelling(), word.getMeaning());
            result.add(item);
        }
        return result;
    }
}