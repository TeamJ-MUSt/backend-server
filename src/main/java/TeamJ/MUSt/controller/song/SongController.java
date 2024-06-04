package TeamJ.MUSt.controller.song;

import TeamJ.MUSt.controller.WordDto;
import TeamJ.MUSt.controller.song.dto.*;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.service.song.SongService;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
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
    @GetMapping("/song/words")
    public List<WordDto> usedWords(@RequestParam("songId") Long songId){
        List<Word> wordsInSong = songRepository.findWithSongWord(songId);
        return wordsInSong.stream().map(WordDto::new).toList();
    }
    @GetMapping("/song/search")
    public SearchResultDtoV2 searchSong(@ModelAttribute SongSearch songSearch, @RequestParam("memberId") Long memberId){
        String title = songSearch.getTitle();
        String artist = songSearch.getArtist();
        List<Tuple> resultSet = songService.searchSong(memberId, title, artist);
        List<SongDtoV2> result = resultSet.stream()
                .map(t -> new SongDtoV2(
                        t.get(0, Song.class),
                        t.get(1, Boolean.class))).toList();

        if(result.isEmpty())
            return new SearchResultDtoV2(null, false);
        else
            return new SearchResultDtoV2(result, true);
    }

    @PostMapping("/song/remote")
    public SearchResultDtoV2 searchRemote(@ModelAttribute SongSearch songSearch){
        String title = songSearch.getTitle();
        String artist = songSearch.getArtist();
        List<Song> findSongs = songService.searchDbSong(title, artist);
        if(!findSongs.isEmpty())
            return new SearchResultDtoV2(null, false);

        List<Song> newSongs = songService.searchRemoteSong(title, artist);
        if(newSongs.isEmpty())
            return new SearchResultDtoV2(null, false);
        List<SongDtoV2> result = newSongs.stream()
                .map(s -> new SongDtoV2(s, false)).toList();
        return new SearchResultDtoV2(result, true);

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


