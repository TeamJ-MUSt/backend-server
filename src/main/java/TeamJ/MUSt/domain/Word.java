package TeamJ.MUSt.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
public class Word {
    public Word() {
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Long id;

    private String spelling;

    private String enPronunciation;

    private String jpPronunciation;

    @OneToMany(mappedBy = "word")
    private List<Meaning> meaning;

    private String classOfWord;

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
    public Word(String spelling, String jpPronunciation, String classOfWord) {
        this.spelling = spelling;
        this.jpPronunciation = jpPronunciation;
        this.classOfWord = classOfWord;
    }

    public void updateId(Long id){
        this.id = id;
    }

    public void updateMeaning(List<Meaning> meaning){
        this.meaning = meaning;
    }

}
