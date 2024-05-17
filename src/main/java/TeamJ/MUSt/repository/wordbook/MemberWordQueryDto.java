package TeamJ.MUSt.repository.wordbook;

import TeamJ.MUSt.controller.WordDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class MemberWordQueryDto {

    private boolean success;

    private List<WordDto> words = new ArrayList<>();

    public MemberWordQueryDto(boolean success, List<WordDto> words) {
        this.success = success;
        this.words = words;
    }

    public MemberWordQueryDto() {
    }
}
