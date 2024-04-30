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

    @Enumerated(EnumType.STRING)
    private Level level;

    @Lob
    private char[] lyric;

    @Lob
    private byte[] thumbnail;

    @OneToMany(mappedBy = "song")
    private List<MemberSong> memberSongs = new ArrayList<>();

    @OneToMany(mappedBy = "song", orphanRemoval = true)
    private List<SongWord> songWords = new ArrayList<>();

    public Song(String title, String artist, String lyrics, byte[] thumbnail) {
        this.title = title;
        this.artist = artist;
        this.lyric = lyrics.toCharArray();
        this.thumbnail = thumbnail;
    }

    public Song() {

    }
}
