package TeamJ.MUSt.repository.songword;

import TeamJ.MUSt.domain.SongWord;

import java.util.List;

public interface SongWordRepositoryCustom {
    void bulkSave(List<SongWord> songWords);
}
