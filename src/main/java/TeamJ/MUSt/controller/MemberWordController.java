package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.wordbook.MemberWordQueryDto;
import TeamJ.MUSt.service.MemberWordService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    @Getter
    static class UpdateResultDto {
        boolean success;

        public UpdateResultDto(boolean success) {
            this.success = success;
        }
    }
}
