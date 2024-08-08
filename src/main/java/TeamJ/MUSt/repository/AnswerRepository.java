package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Answer;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AnswerRepository {
    public static final String INSERT_QUERY = "insert into answer (quiz_id, content) values (?, ?)";
    public static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    public AnswerRepository(DataSource dataSource, EntityManager entityManager){
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }

    public void bulkSaveAnswer(List<Answer> answers){

        for(int i = 0; i < answers.size(); i+= BATCH_SIZE) {

            List<Answer> batchList = answers.subList(i, Math.min(i + BATCH_SIZE, answers.size()));

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
