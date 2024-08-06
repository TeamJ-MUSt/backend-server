package TeamJ.MUSt.repository.songword;

import TeamJ.MUSt.domain.SongWord;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

public class SongWordRepositoryImpl implements SongWordRepositoryCustom{
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;
    public SongWordRepositoryImpl(DataSource dataSource, EntityManager entityManager){
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }
    @Transactional
    @Override
    public void bulkSaveSongWord(List<SongWord> songWords) {
        String sql = "insert into song_word (song_id, word_id, surface) values (? ,? ,?)";
        int batchSize = 1000;
        for(int i = 0; i < songWords.size(); i+= batchSize) {
            List<SongWord> batchList = songWords.subList(i, Math.min(i + batchSize, songWords.size()));
            jdbcTemplate.batchUpdate(
                    sql,
                    batchList,
                    batchSize,
                    (ps, argument) -> {
                        ps.setLong(1, argument.getSong().getId());
                        ps.setLong(2, argument.getWord().getId());
                        ps.setString(3, argument.getSurface());
                    });
        }
        em.flush();
    }
}
