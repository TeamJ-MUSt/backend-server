package TeamJ.MUSt.controller.song.dto;

import lombok.Getter;

@Getter
public class SongDto{
    Long songId;
    String title;
    String artist;
    String lyrics;
    Boolean success;
    public SongDto(Long songId, String title, String artist, String lyrics, Boolean success) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.lyrics = lyrics;
        this.success = true;
    }

    public SongDto() {
    }
}