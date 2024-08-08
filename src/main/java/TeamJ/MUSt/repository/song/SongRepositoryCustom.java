package TeamJ.MUSt.repository.song;

import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.domain.Word;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepositoryCustom {
    //쿼리 메소드로 처리 가능할 듯
    List<Word> findWithSongWordById(Long songId);

    List<Song> findRequestSong(String title, String artist);

    int countSearchResult(String title, String artist);

    //쿼리 메소드로 처리 가능할 듯
    List<Song> findWithMemberSong(@Param("memberId") Long memberId);

    List<Tuple> findWithMemberSongCheckingRegister(Long memberId, String title, String artist);
}
