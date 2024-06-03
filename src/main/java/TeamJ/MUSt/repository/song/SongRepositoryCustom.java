package TeamJ.MUSt.repository.song;

import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.domain.Word;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SongRepositoryCustom {
    List<Word> findUsedWords(Long songId);

    List<Song> findInMySong(Long memberId, String title, String artist);
    List<Song> findRequestSong(String title, String artist);

    int countSearchResult(String title, String artist);
    List<Song> findWithMemberSong(Long memberId);
    List<Song> findWithMemberSong(Long memberId, Pageable pageable);

    List<Tuple> findWithMemberSongCheckingRegister(Long memberId, String title, String artist);
}
