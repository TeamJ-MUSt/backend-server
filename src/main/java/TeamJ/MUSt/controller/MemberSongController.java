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
        try {
            memberSongService.deleteSongInList(memberId, songId);
        } catch (Exception e) {
            return "삭제 실패";
        }
        return "성공적으로 삭제";
    }
}
