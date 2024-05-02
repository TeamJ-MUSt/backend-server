package TeamJ.MUSt.controller.song.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResultDto {
    List<SongDto> songs;
    boolean success;

    public SearchResultDto(List<SongDto> songs, boolean success) {
        this.songs = songs;
        this.success = success;
    }
}
