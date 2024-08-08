package TeamJ.MUSt.repository.songword;

import TeamJ.MUSt.domain.SongWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongWordRepository extends JpaRepository<SongWord, Long>, SongWordRepositoryCustom {

    @Query("select sw from SongWord sw join fetch sw.word where sw.song.id = :songId")
    List<SongWord> findWithWordBySongId(@Param("songId") Long songId);
}
