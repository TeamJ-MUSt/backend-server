package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Word {
    @Id @GeneratedValue
    @Column(name = "word_id")
    private Long id;

    private String spelling;

    private String enPronunciation;

    private String jpPronunciation;

    private String meaning;

    private String classOfWord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "word")
    List<SongWord> songWords = new ArrayList<>();
}
