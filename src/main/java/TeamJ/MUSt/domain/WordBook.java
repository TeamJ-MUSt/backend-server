package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class WordBook {
    @Id @GeneratedValue
    @Column(name = "user_word_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    public WordBook(Member member, Word word) {
        this.member = member;
        this.word = word;
    }
    public WordBook(){

    }
}
