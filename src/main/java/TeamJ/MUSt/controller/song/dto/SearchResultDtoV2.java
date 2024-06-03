package TeamJ.MUSt.controller.song.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResultDtoV2 {
    List<SongDtoV2> songs;
    boolean success;

    public SearchResultDtoV2(List<SongDtoV2> songs, boolean success) {
        this.songs = songs;
        this.success = success;
    }
}
