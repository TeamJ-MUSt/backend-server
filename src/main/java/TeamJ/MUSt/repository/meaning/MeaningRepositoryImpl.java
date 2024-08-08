package TeamJ.MUSt.repository.meaning;

import TeamJ.MUSt.domain.Meaning;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MeaningRepositoryImpl implements MeaningRepositoryCustom{

    public static final int BATCH_SIZE = 1000;
    public static final String INSERT_QUERY = "insert into meaning (word_id, content) values (?, ?)";
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;


    public MeaningRepositoryImpl(DataSource dataSource, EntityManager entityManager){
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }

    @Transactional
    public void bulkSave(List<Meaning> meanings){
        for(int i = 0; i < meanings.size(); i+= BATCH_SIZE) {
            List<Meaning> batchList = meanings.subList(i, Math.min(i + BATCH_SIZE, meanings.size()));
            jdbcTemplate.batchUpdate(
                    INSERT_QUERY,
                    batchList,
                    BATCH_SIZE,
                    (ps, argument) -> {
                        ps.setLong(1, argument.getWord().getId());
                        ps.setString(2, argument.getContent());
                    });
        }
        em.flush();
    }
}
