package TeamJ.MUSt.repository.song;

import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long>, SongRepositoryCustom {
    @Query("select sw.word from Song s join s.songWords sw where s.id = :songId")
    List<Word> findWithSongWord(@Param("songId") Long songId);
}
