package TeamJ.MUSt.controller.song.dto;

import TeamJ.MUSt.domain.Song;
import lombok.Getter;

@Getter
public class SongDtoV2 {
    Long songId;
    String title;
    String artist;
    String lyrics;
    Integer level;
    String bugsId;

    boolean hasSong;
    public SongDtoV2(Song song, boolean hasSong) {
        this.songId = song.getId();
        this.title = song.getTitle();
        this.artist = song.getArtist();
        this.level = song.getLevel();
        this.hasSong = hasSong;
        this.bugsId = song.getBugsId();
    }

    public SongDtoV2() {
    }
}