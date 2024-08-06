package TeamJ.MUSt.repository.songword;

import TeamJ.MUSt.domain.SongWord;

import java.util.List;

public interface SongWordRepositoryCustom {
    void bulkSaveSongWord(List<SongWord> songWords);
}
