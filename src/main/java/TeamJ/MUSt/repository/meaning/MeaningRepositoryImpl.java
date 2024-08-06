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
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    public MeaningRepositoryImpl(DataSource dataSource, EntityManager entityManager){
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }

    @Transactional
    public void bulkSaveMeaning(List<Meaning> meanings){
        String sql = "insert into meaning (word_id, meaning) values (?, ?)";
        int batchSize = 1000;
        for(int i = 0; i < meanings.size(); i+= batchSize) {
            List<Meaning> batchList = meanings.subList(i, Math.min(i + batchSize, meanings.size()));
            jdbcTemplate.batchUpdate(
                    sql,
                    batchList,
                    batchSize,
                    (ps, argument) -> {
                        ps.setLong(1, argument.getWord().getId());
                        ps.setString(2, argument.getMeaning());
                    });
        }
        em.flush();
    }
}
