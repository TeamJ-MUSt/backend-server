package TeamJ.MUSt.controller.song;

import TeamJ.MUSt.controller.song.dto.*;
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
    public HomeDto songs(@PathVariable("memberId") Long memberId){
        List<Song> userSong = songService.findUserSong(memberId);
        List<SongDto> list = userSong.stream()
                .map(s -> new SongDto(
                        s.getId(),
                        s.getTitle(),
                        s.getArtist(),
                        new String(s.getLyric()),
                        s.getLevel())
                ).toList();
        return new HomeDto(list, 5, 5);

    }

    @GetMapping("/{memberId}/search")
    public SearchResultDto searchOwnSong(@PathVariable("memberId") Long memberId, @ModelAttribute SongSearch songSearch){
        String title = songSearch.getTitle();
        String artist = songSearch.getArtist();
        System.out.println("title = " + title);
        List<Song> searchedSong = null;
        searchedSong = songService.searchMySong(memberId, title, artist);

        if(searchedSong.isEmpty())
            return new SearchResultDto(null, false);

        return new SearchResultDto(
                searchedSong.stream().map(song -> new SongDto(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist(),
                        new String(song.getLyric()),
                        song.getLevel()
                        )).toList(),
                true);
    }

    @GetMapping("/search")
    public SearchResultDto searchInDb(@ModelAttribute SongSearch songSearch){
        String title = songSearch.getTitle();
        String artist = songSearch.getArtist();
        List<Song> searchedSong = null;
        searchedSong = songService.searchDbSong(title, artist);

        if(searchedSong.isEmpty())
            return new SearchResultDto(null, false);

        return new SearchResultDto(
                searchedSong.stream().map(song -> new SongDto(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist(),
                        new String(song.getLyric()),
                        song.getLevel()
                        )).toList(),
                true);
    }

    @PostMapping("/songs/new")
    public String register(@ModelAttribute RegisterDto registerDto) throws NoSearchResultException {
        return songService.registerSong(registerDto.getMemberId(), registerDto.getSongId());
    }

    @GetMapping(value = "/image/{songId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] search(@PathVariable("songId") Long songId){
        Song song = songRepository.findById(songId).get();
        return song.getThumbnail();
    }
}


