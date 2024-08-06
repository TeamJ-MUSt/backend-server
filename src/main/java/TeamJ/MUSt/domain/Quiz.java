package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Quiz {
    public Quiz() {
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id")
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    @Enumerated(EnumType.STRING)
    private QuizType type;

    @OneToMany(mappedBy = "quiz")
    private List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "quiz")
    private List<Choice> choices = new ArrayList<>();

    public Quiz(Song song, Word word, QuizType type, List<Answer> answers, List<Choice> choices) {
        this.song = song;
        this.word = word;
        this.type = type;
        this.answers = answers;
        this.choices = choices;
    }
    public Quiz(Song song, Word word, QuizType type) {
        this.song = song;
        this.word = word;
        this.type = type;
    }

    public Quiz(Song song, QuizType type, List<Answer> answers, List<Choice> choices) {
        this.song = song;
        this.type = type;
        this.answers = answers;
        this.choices = choices;
    }

    public void initId(Long id){
        this.id = id;
    }
}
