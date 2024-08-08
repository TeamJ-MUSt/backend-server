package TeamJ.MUSt.controller.song;

import TeamJ.MUSt.controller.WordDto;
import TeamJ.MUSt.controller.song.dto.*;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.exception.NoSearchResultException;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.service.song.SongService;
import com.querydsl.core.Tuple;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class SongController {
    private final SongService songService;
    private final SongRepository songRepository;


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSearchResultException.class)
    public SearchResultDto noSearchResult(){
        return new SearchResultDto(null, false);
    }

    @GetMapping(value = "/main/songs")
    public HomeDto songs(@SessionAttribute(name = "memberId", required = false) Long memberId) {

        List<Song> songsOfMember = songService.findSongOfMember(memberId);

        List<MainSongDto> mainSongDtos = songsOfMember
                .stream()
                .map(s -> new MainSongDto(
                        s.getId(),
                        s.getTitle(),
                        s.getArtist(),
                        new String(s.getLyric()),
                        s.getLevel())
                ).toList();

        return new HomeDto(mainSongDtos, 5, 5);

    }

    @GetMapping("/song/words")
    public List<WordDto> usedWords(@RequestParam("songId") Long songId) {

        List<Word> wordsInSong = songRepository.findWithSongWordBySongId(songId);

        return wordsInSong
                .stream()
                .map(WordDto::new)
                .toList();
    }

    @GetMapping("/song/search")
    public SearchResultDto searchSong(
            @ModelAttribute SongSearch songSearch,
            @SessionAttribute(name = "memberId", required = false) Long memberId
    ) {

        String title = songSearch.getTitle();
        String artist = songSearch.getArtist();

        List<Tuple> foundSongsInDb = songService.searchSongWithCheckingRegisterStatus(memberId, title, artist);

        List<SearchedSongDto> foundSongDtos = foundSongsInDb.stream()
                .map(
                        t -> new SearchedSongDto(
                                t.get(0, Song.class),
                                t.get(1, Boolean.class)
                        )
                )
                .toList();

        if (hasNoRequestSongInDb(foundSongDtos))
            return new SearchResultDto(null, false);

        return new SearchResultDto(foundSongDtos, true);
    }

    private static boolean hasNoRequestSongInDb(List<SearchedSongDto> searchedSongDtos) {
        return searchedSongDtos.isEmpty();
    }

    @PostMapping("/song/remote")
    public SearchResultDto searchRemote(@ModelAttribute SongSearch songSearch) throws NoSearchResultException {

        String title = songSearch.getTitle();
        String artist = songSearch.getArtist();

        List<Song> searchedSongsFromWeb = songService.searchSongRemotely(title, artist);

        List<SearchedSongDto> result = searchedSongsFromWeb
                .stream()
                .map(s -> new SearchedSongDto(s, false))
                .toList();

        return new SearchResultDto(result, true);

    }

    @PostMapping("/songs/new")
    public RegisterResultDto register(
            @ModelAttribute RegisterDto registerDto,
            @SessionAttribute(name = "memberId", required = false) Long memberId
    ) throws IOException {

        boolean registerResult = songService.registerSong(memberId, registerDto.getSongId(), registerDto.getBugsId());

        return new RegisterResultDto(registerResult);
    }

    @GetMapping(value = "/image/{songId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] search(@PathVariable("songId") Long songId) {

        Song findSong = songRepository.findById(songId).get();

        return findSong.getThumbnail();
    }

    @GetMapping(value = "/image/small/{songId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] image(@PathVariable("songId") Long songId) {

        Song findSong = songRepository.findById(songId).get();

        return findSong.getSmallThumbnail();
    }

    @Getter
    @Setter
    static class RegisterResultDto{
        boolean success;

        public RegisterResultDto(boolean success){
            this.success = success;
        }
    }
}


