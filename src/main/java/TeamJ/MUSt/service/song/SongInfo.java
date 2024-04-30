package TeamJ.MUSt.service.song;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SongInfo{
    private String music_id;
    private String title;
    private String artist;
    private String thumbnailUrl;
    private String lyrics;

    public SongInfo(String musicId, String title, String artist, String thumbnailUrl, String lyrics) {
        this.music_id = musicId;
        this.title = title;
        this.artist = artist;
        this.thumbnailUrl = thumbnailUrl;
        this.lyrics = lyrics;
    }

    public SongInfo() {
    }
}