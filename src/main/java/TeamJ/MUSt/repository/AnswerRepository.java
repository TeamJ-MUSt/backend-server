package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Answer;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AnswerRepository {
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    public AnswerRepository(DataSource dataSource, EntityManager entityManager){
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }

    public void bulkSaveAnswer(List<Answer> answers){
        String sql = "insert into answer (quiz_id, answer) values (?, ?)";
        int batchSize = 1000;
        for(int i = 0; i < answers.size(); i+= batchSize) {
            List<Answer> batchList = answers.subList(i, Math.min(i + batchSize, answers.size()));
            jdbcTemplate.batchUpdate(
                    sql,
                    batchList,
                    batchSize,
                    (ps, argument) -> {
                        ps.setLong(1, argument.getQuiz().getId());
                        ps.setString(2, argument.getAnswer());
                    });
        }
        em.flush();
    }
}
