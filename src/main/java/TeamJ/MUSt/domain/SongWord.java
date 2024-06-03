package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class SongWord {
    @Id @GeneratedValue
    @Column(name = "song_word_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id")
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    private String surface;
    public void createSongWord(Song song, Word word, String surface){
        song.getSongWords().add(this);
        word.getSongWords().add(this);
        this.song = song;
        this.word = word;
        this.surface = surface;
    }
}
