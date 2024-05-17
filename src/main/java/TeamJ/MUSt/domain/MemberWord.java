package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class MemberWord {
    @Id @GeneratedValue
    @Column(name = "member_word_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    public MemberWord(Member member, Word word) {
        this.member = member;
        this.word = word;
    }
    public MemberWord(){

    }
}
