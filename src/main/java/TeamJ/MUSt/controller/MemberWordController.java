package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.wordbook.MemberWordQueryDto;
import TeamJ.MUSt.service.MemberWordService;
import TeamJ.MUSt.util.WordInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class MemberWordController {
    private final MemberWordService memberWordService;

    @GetMapping("/word-book/{memberId}")
    public MemberWordQueryDto words(@PathVariable("memberId") Long memberId){
        List<Word> userWord = memberWordService.findUserWord(memberId);
        List<WordDto> result = userWord.stream().map(WordDto::new).toList();
        if(result.isEmpty())
            return new MemberWordQueryDto();
        else
            return new MemberWordQueryDto(true, result);
    }

    @PostMapping("/word-book/delete")
    public UpdateResultDto delete(@RequestParam("memberId") Long memberId, @RequestParam("songId") Long songId){
        long deleted = memberWordService.deleteWord(memberId, songId);
        if(deleted == 0)
            return new UpdateResultDto(false);
        else
            return new UpdateResultDto(true);

    }

    @PostMapping("/word-book/new")
    public UpdateResultDto registerWord(@RequestParam("memberId") Long memberId, @RequestParam("songId") Long songId){
        boolean result = memberWordService.register(memberId, songId);
        return new UpdateResultDto(result);
    }

    @GetMapping("/word-book/word/{wordId}/similar")
    public SimilarQueryDto similarWord(@PathVariable("wordId") Long wordId, @RequestParam("num") Integer num) throws IOException {
        List<WordInfo> similarWords = memberWordService.similarWords(wordId, num);
        List<SimilarWordDto> list = similarWords.stream().map(w ->
                new SimilarWordDto(
                        w.getLemma(),
                        w.getPronunciation(),
                        w.getSpeechFields(),
                        w.getMeaning())).toList();
        if(list.isEmpty())
            return new SimilarQueryDto();
        else
            return new SimilarQueryDto(true, list);
    }

    @Getter
    static class UpdateResultDto {
        boolean success;

        public UpdateResultDto(boolean success) {
            this.success = success;
        }
    }

    @Getter
    static class SimilarWordDto{
        private String spell;
        private String japPro;
        private String classOfWord;
        private List<String> meaning;

        public SimilarWordDto(String spell, String japPro, String classOfWord, List<String> meaning) {
            this.spell = spell;
            this.japPro = japPro;
            this.classOfWord = classOfWord;
            this.meaning = meaning;
        }
    }

    @Getter
    static class SimilarQueryDto{
        private boolean success;
        private List<SimilarWordDto> similarWordDtoList;

        public SimilarQueryDto(boolean success, List<SimilarWordDto> similarWordDtoList) {
            this.success = success;
            this.similarWordDtoList = similarWordDtoList;
        }

        public SimilarQueryDto() {
        }
    }
}
