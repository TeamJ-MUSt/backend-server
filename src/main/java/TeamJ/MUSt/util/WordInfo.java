package TeamJ.MUSt.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class WordInfo{
    private String surface;
    private String speechFields;
    private String pronunciation;
    private String lemma;
    private List<String> meaning = new ArrayList<>();

    public WordInfo(String surface, String speechFields, String pronunciation, String lemma) {
        this.surface = surface;
        this.speechFields = speechFields;
        this.pronunciation = pronunciation;
        this.lemma = lemma;
    }

    public WordInfo() {
    }
}