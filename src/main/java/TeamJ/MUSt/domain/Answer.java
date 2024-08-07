package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Answer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    public Answer(String answer, Quiz quiz) {
        this.content = answer;
        this.quiz = quiz;
    }

    public Answer() {

    }
}
