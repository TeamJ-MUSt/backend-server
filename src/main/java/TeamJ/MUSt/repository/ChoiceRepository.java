package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Choice;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChoiceRepository {
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    public ChoiceRepository(DataSource dataSource, EntityManager entityManager){
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }

    public void bulkSaveChoice(List<Choice> choices){
        String sql = "insert into choice (quiz_id, choice) values (?, ?)";
        int batchSize = 1000;
        for(int i = 0; i < choices.size(); i+= batchSize) {
            List<Choice> batchList = choices.subList(i, Math.min(i + batchSize, choices.size()));
            jdbcTemplate.batchUpdate(
                    sql,
                    batchList,
                    batchSize,
                    (ps, argument) -> {
                        ps.setLong(1, argument.getQuiz().getId());
                        ps.setString(2, argument.getChoice());
                    });
        }
        em.flush();
    }

}
