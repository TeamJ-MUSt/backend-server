package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Meaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meaning_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    public Meaning(String content) {
        this.content = content;
    }
    public Meaning(String content, Word word) {
        this.content = content;
        this.word = word;
    }
    public Meaning() {
    }

    public void updateWord(Word word){
        this.word = word;
    }
}
