package TeamJ.MUSt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class AllFieldWordInfo{
    private String lemma;
    private List<String> speechFields;
    private String pronunciation;
    private List<String> meaning = new ArrayList<>();

    public AllFieldWordInfo(List<String> speechFields, String pronunciation, String lemma) {
        this.speechFields = speechFields;
        this.pronunciation = pronunciation;
        this.lemma = lemma;
    }

    public AllFieldWordInfo() {
    }
}