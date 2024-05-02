package TeamJ.MUSt.controller.song.dto;

import lombok.Getter;

@Getter
public class SongDto{
    Long songId;
    String title;
    String artist;
    String lyrics;
    public SongDto(Long songId, String title, String artist, String lyrics) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.lyrics = lyrics;
    }

    public SongDto() {
    }
}