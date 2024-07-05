package TeamJ.MUSt.controller.song.dto;

import lombok.Getter;

@Getter
public class MainSongDto {
    Long songId;
    String title;
    String artist;
    String lyrics;
    Integer level;
    public MainSongDto(Long songId, String title, String artist, String lyrics, Integer level) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.lyrics = lyrics;
        this.level = level;
    }

    public MainSongDto() {
    }
}