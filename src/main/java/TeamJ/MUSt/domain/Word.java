package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Word {
    public Word() {
    }

    @Id @GeneratedValue
    @Column(name = "word_id")
    private Long id;

    private String spelling;

    private String enPronunciation;

    private String jpPronunciation;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meaning> meaning;

    private String classOfWord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "word")
    List<SongWord> songWords = new ArrayList<>();

    @OneToMany(mappedBy = "word")
    List<MemberWord> memberWords = new ArrayList<>();

    public Word(String spelling, String jpPronunciation, List<Meaning> meaning, String classOfWord) {
        this.spelling = spelling;
        this.jpPronunciation = jpPronunciation;
        this.meaning = meaning;
        this.classOfWord = classOfWord;
    }
}
