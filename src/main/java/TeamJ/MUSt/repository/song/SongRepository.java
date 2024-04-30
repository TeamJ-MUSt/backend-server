package TeamJ.MUSt.repository.song;

import TeamJ.MUSt.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long>, SongRepositoryCustom {
    List<Song> findByTitleContaining(String title);
}
