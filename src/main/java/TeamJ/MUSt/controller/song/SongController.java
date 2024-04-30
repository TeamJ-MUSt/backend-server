package TeamJ.MUSt.controller.song;

import TeamJ.MUSt.controller.song.dto.HomeDto;
import TeamJ.MUSt.controller.song.dto.SongDto;
import TeamJ.MUSt.controller.song.dto.SongSearch;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.service.song.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class SongController {
    private final SongService songService;
    private final SongRepository songRepository;

    @GetMapping(value = "/main/songs/{memberId}")
    public HomeDto songs(@PathVariable("memberId") Long memberId, @RequestParam("pageNum") int pageNum){
        PageRequest pageRequest = PageRequest.of(pageNum, 6);
        List<Song> userSong = songService.findUserSong(memberId, pageRequest);
        List<SongDto> list = userSong.stream()
                .map(s -> new SongDto(
                        s.getId(),
                        s.getTitle(),
                        s.getArtist(),
                        new String(s.getLyric()),
                        true)
                ).toList();
        return new HomeDto(list, 5, 5);

    }

    @GetMapping("/{memberId}/search")
    public SongDto search(@PathVariable("memberId") Long memberId, @ModelAttribute SongSearch songSearch){
        String title = songSearch.getTitle();
        String artist = songSearch.getArtist();
        Song findSong = null;
        try {
            findSong = songService.searchSong(memberId, title, artist);
        } catch (NoSearchResultException e) {
            return new SongDto();
        }
        return new SongDto(findSong.getId(), findSong.getTitle(), findSong.getArtist(), new String(findSong.getLyric()), true);
    }

    @PostMapping("/songs/{userId}/{songId}")
    public String register(@PathVariable("userId") Long userId, @PathVariable("songId") Long songId){
        return songService.registerSong(userId, songId);
    }

    @GetMapping(value = "/image/{songId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] search(@PathVariable("songId") Long songId){
        Song song = songRepository.findById(songId).get();
        return song.getThumbnail();
    }
}


