package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
public class Song {
    @Id @GeneratedValue
    private Long id;

    private String title;

    private String artist;

    private Integer level;

    @Lob
    private char[] lyric;

    @Lob
    private byte[] thumbnail;

    @Lob
    private byte[] smallThumbnail;

    @OneToMany(mappedBy = "song")
    private List<MemberSong> memberSongs = new ArrayList<>();

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<SongWord> songWords = new ArrayList<>();

    private String bugsId;
    public Song(String title, String artist, byte[] smallThumbnail, byte[] thumbnail, String bugsId) {
        this.title = title;
        this.artist = artist;
        this.smallThumbnail = smallThumbnail;
        this.thumbnail = thumbnail;
        this.bugsId = bugsId;
    }

    public Song() {

    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setLyric(char[] lyric) {
        this.lyric = lyric;
    }
    public void setThumbnail(byte[] image){this.thumbnail = image;}
}
