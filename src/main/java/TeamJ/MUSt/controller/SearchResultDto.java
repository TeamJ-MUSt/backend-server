package TeamJ.MUSt.controller;

import TeamJ.MUSt.controller.song.dto.SongDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SearchResultDto {
    List<SongDto> songs = new ArrayList<>();

    public SearchResultDto(List<SongDto> songs) {
        this.songs = songs;
    }
}
