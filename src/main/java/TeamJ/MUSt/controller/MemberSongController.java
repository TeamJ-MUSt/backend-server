package TeamJ.MUSt.controller;

import TeamJ.MUSt.repository.MemberSongRepository;
import TeamJ.MUSt.service.MemberSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class MemberSongController {
    //private final MemberSongRepository memberSongRepository;
    private final MemberSongService memberSongService;
    @PostMapping("/songs/delete")
    public String delete(@RequestParam("memberId") Long memberId, @RequestParam("songId") Long songId){
        try{
            //memberSongRepository.deleteByMemberIdAndSongId(memberId, songId);
            memberSongService.deleteSongInList(memberId, songId);
        }catch (Exception e){
            return "삭제 실패";
        }
        return "성공적으로 삭제";
    }
}
