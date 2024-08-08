package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Choice;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChoiceRepository {
    public static final String INSERT_QUERY = "insert into choice (quiz_id, content) values (?, ?)";
    public static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    public ChoiceRepository(DataSource dataSource, EntityManager entityManager){
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }

    public void bulkSaveChoice(List<Choice> choices){
        for(int i = 0; i < choices.size(); i+= BATCH_SIZE) {
            List<Choice> batchList = choices.subList(i, Math.min(i + BATCH_SIZE, choices.size()));

            jdbcTemplate.batchUpdate(
                    INSERT_QUERY,
                    batchList,
                    BATCH_SIZE,
                    (ps, argument) -> {
                        ps.setLong(1, argument.getQuiz().getId());
                        ps.setString(2, argument.getContent());
                    });

        }

        em.flush();
    }

}
