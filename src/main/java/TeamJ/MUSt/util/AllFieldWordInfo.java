package TeamJ.MUSt.util;
import TeamJ.MUSt.domain.Meaning;
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
    private String speechFields;
    private String pronunciation;
    private List<String> meaning = new ArrayList<>();

    public AllFieldWordInfo(String speechFields, String pronunciation, String lemma, List<Meaning> meanings) {
        this.speechFields = speechFields;
        this.pronunciation = pronunciation;
        this.lemma = lemma;
        this.meaning = meanings.stream().map(m -> m.getContent()).toList();
    }

    public AllFieldWordInfo() {
    }
}
