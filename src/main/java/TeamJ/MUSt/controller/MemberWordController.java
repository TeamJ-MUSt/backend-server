package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Meaning;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.wordbook.MemberWordQueryDto;
import TeamJ.MUSt.service.MemberWordService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class MemberWordController {
    private final MemberWordService memberWordService;

    @GetMapping("/word-book")
    public MemberWordQueryDto words(@SessionAttribute(name = "memberId", required = false) Long memberId) {
        
        List<Word> studiedWords = memberWordService.findUserWord(memberId);

        if (hasStudiedWords(studiedWords)){
            List<WordDto> studiedWordsDto = studiedWords.stream().map(WordDto::new).toList();

            return new MemberWordQueryDto(true, studiedWordsDto);
        }

        return new MemberWordQueryDto();
    }

    @PostMapping("/word-book/delete")
    public UpdateResultDto delete(
            @SessionAttribute(name = "memberId", required = false) Long memberId,
            @RequestParam("songId") Long songId
    ) {

        long deletedNum = memberWordService.deleteWord(memberId, songId);

        if (deletedNum == 0)
            return new UpdateResultDto(false);

        return new UpdateResultDto(true);

    }

    @PostMapping("/word-book/new")
    public UpdateResultDto registerWord(
            @SessionAttribute(name = "memberId", required = false) Long memberId,
            @RequestParam("songId") Long songId
    ) {

        boolean addResult = memberWordService.register(memberId, songId);

        return new UpdateResultDto(addResult);
    }

    @GetMapping("/word-book/word/similar/{wordId}")
    public SimilarQueryDto similarWord(
            @PathVariable("wordId") Long wordId,
            @RequestParam("num") Integer num
    ) throws IOException {

        List<Word> similarWords = memberWordService.getSimilarWords(wordId, num);

        List<SimilarWordDto> similarWordDtos = similarWords.stream().map(w ->
                new SimilarWordDto(
                        w.getSpelling(),
                        w.getJpPronunciation(),
                        w.getClassOfWord(),
                        w.getMeaning().stream().map(Meaning::getContent).toList())).toList();

        if (hasSimilarWords(similarWordDtos))
            return new SimilarQueryDto();

        return new SimilarQueryDto(true, similarWordDtos);
    }



    @Getter
    static class UpdateResultDto {
        boolean success;

        public UpdateResultDto(boolean success) {
            this.success = success;
        }
    }

    @Getter
    static class SimilarWordDto {
        private final String spell;
        private final String japPro;
        private final String classOfWord;
        private final List<String> meaning;

        public SimilarWordDto(String spell, String japPro, String classOfWord, List<String> meaning) {
            this.spell = spell;
            this.japPro = japPro;
            this.classOfWord = classOfWord;
            this.meaning = meaning;
        }
    }

    @Getter
    static class SimilarQueryDto {
        private boolean success;
        private List<SimilarWordDto> similarWordDtoList;

        public SimilarQueryDto(boolean success, List<SimilarWordDto> similarWordDtoList) {
            this.success = success;
            this.similarWordDtoList = similarWordDtoList;
        }

        public SimilarQueryDto() {
        }
    }

    private static boolean hasStudiedWords(List<Word> studiedWords) {
        if(studiedWords.isEmpty())
            return false;

        return true;
    }

    private static boolean hasSimilarWords(List<SimilarWordDto> similarWordDtos) {
        if(similarWordDtos.isEmpty())
            return false;

        return true;
    }
}
