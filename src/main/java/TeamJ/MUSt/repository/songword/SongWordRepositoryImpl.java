package TeamJ.MUSt.repository.songword;

import TeamJ.MUSt.domain.SongWord;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

public class SongWordRepositoryImpl implements SongWordRepositoryCustom{
    public static final String INSERT_QUERY = "insert into song_word (song_id, word_id, surface) values (? ,? ,?)";
    public static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;
    public SongWordRepositoryImpl(DataSource dataSource, EntityManager entityManager){
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }
    @Transactional
    @Override
    public void bulkSave(List<SongWord> songWords) {

        for(int i = 0; i < songWords.size(); i+= BATCH_SIZE) {

            List<SongWord> batchList = getBatchingList(songWords, i);

            jdbcTemplate.batchUpdate(
                    INSERT_QUERY,
                    batchList,
                    BATCH_SIZE,
                    (ps, argument) -> {
                        ps.setLong(1, argument.getSong().getId());
                        ps.setLong(2, argument.getWord().getId());
                        ps.setString(3, argument.getSurface());
                    });

        }

        em.flush();
    }

    private static List<SongWord> getBatchingList(List<SongWord> songWords, int start) {

        int end = Math.min(start + BATCH_SIZE, songWords.size());

        return songWords.subList(start, end);

    }
}
