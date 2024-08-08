package TeamJ.MUSt.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MeaningResult {
    private String word;
    private List<String> definitions;
    private String pronunciation;
    private int level;
    public MeaningResult(){
    }
}