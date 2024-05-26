package TeamJ.MUSt.controller;

import TeamJ.MUSt.domain.Meaning;
import TeamJ.MUSt.domain.Word;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class WordDto {
    private Long id;
    private String spell;
    private String enPro;
    private String japPro;
    private String classOfWord;
    private List<String> meaning;
    private List<String> involvedSongs = new ArrayList<>();

    public WordDto(Word word) {
        this.id = word.getId();
        this.spell = word.getSpelling();
        this.enPro = word.getEnPronunciation();
        this.japPro = word.getJpPronunciation();
        this.classOfWord = word.getClassOfWord();
        this.meaning = word.getMeaning().stream().map(Meaning::getMeaning).toList();
        this.involvedSongs = word.getSongWords()
                .stream().map(songWord ->songWord.getSong().getTitle()).toList();
    }
}
