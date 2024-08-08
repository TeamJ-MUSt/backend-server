package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Choice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    public Choice(String choice, Quiz quiz) {
        this.content = choice;
        this.quiz = quiz;
    }

    public Choice() {
    }

}
