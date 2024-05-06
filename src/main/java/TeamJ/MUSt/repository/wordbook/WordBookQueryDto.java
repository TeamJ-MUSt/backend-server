package TeamJ.MUSt.repository.wordbook;

import TeamJ.MUSt.domain.Meaning;
import TeamJ.MUSt.domain.WordBook;

import java.util.ArrayList;
import java.util.List;

public class WordBookQueryDto {
    private String spell;
    private String enPro;
    private String japPro;
    private String classOfWord;
    private List<Meaning> meaning;
    private List<String> involvedSongs = new ArrayList<>();

    public WordBookQueryDto(WordBook wordBook) {
        this.spell = wordBook.getWord().getSpelling();
        this.enPro = wordBook.getWord().getEnPronunciation();
        this.japPro = wordBook.getWord().getJpPronunciation();
        this.meaning = wordBook.getWord().getMeaning();
        this.involvedSongs = wordBook.getWord().getSongWords()
                .stream().map(songWord ->songWord.getSong().getTitle()).toList();
    }
}
