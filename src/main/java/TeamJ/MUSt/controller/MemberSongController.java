package TeamJ.MUSt.controller;

import TeamJ.MUSt.service.MemberSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class MemberSongController {
    private final MemberSongService memberSongService;

    @PostMapping("/songs/delete")
    public String delete(@SessionAttribute(name = "memberId", required = false) Long memberId,
                         @RequestParam("songId") Long songId) {

        int deletedNum = memberSongService.deleteSongInList(memberId, songId);

        if(deletedNum > 0)
            return "성공적으로 삭제";

        return "삭제할 대상이 없음";
    }
}
