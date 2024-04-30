package TeamJ.MUSt.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class WordSearch {
    @NotEmpty
    private String spell;

    @NotEmpty
    private String meaning;
}
