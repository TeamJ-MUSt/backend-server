package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Quiz {
    @Id @GeneratedValue
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
}
