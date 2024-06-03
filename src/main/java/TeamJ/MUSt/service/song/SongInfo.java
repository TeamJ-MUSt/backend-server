package TeamJ.MUSt.service.song;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SongInfo{
    private String music_id;
    private String title;
    private String artist;
    private String thumbnailUrl;
    private String lyrics;
    private String thumbnailUrl_large;
    public SongInfo(String musicId, String title, String artist, String thumbnailUrl, String lyrics, String thumbnailUrl_large) {
        this.music_id = musicId;
        this.title = title;
        this.artist = artist;
        this.thumbnailUrl = thumbnailUrl;
        this.lyrics = lyrics;
        this.thumbnailUrl_large = thumbnailUrl_large;
    }

    public SongInfo() {
    }
}