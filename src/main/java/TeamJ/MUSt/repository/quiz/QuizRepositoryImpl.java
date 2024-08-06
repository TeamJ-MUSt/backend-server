package TeamJ.MUSt.repository.quiz;

import TeamJ.MUSt.domain.Quiz;
import TeamJ.MUSt.domain.QuizType;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import static TeamJ.MUSt.domain.QQuiz.quiz;

public class QuizRepositoryImpl implements QuizRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;
    public QuizRepositoryImpl(EntityManager entityManager, DataSource dataSource) {
        queryFactory = new JPAQueryFactory(entityManager);
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }

    @Override
    public Page<Quiz> findQuizSet(Long songId, QuizType type, Pageable page) {
        List<Quiz> content = queryFactory
                .select(quiz)
                .from(quiz)
                .join(quiz.word).fetchJoin()
                .where(quiz.song.id.eq(songId), quiz.type.eq(type))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();
        JPAQuery<Quiz> countQuery = queryFactory
                .select(quiz)
                .from(quiz)
                .where(quiz.song.id.eq(songId), quiz.type.eq(type));
        return PageableExecutionUtils.getPage(content, page, countQuery::fetchCount);
    }

    @Override
    public void bulkSaveQuiz(List<Quiz> quizzes) {
        String sql = "insert into quiz (song_id, word_id, type) values (?, ?, ?)";
        int batchSize = 1000;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        for (int i = 0; i < quizzes.size(); i += batchSize) {
            List<Quiz> batchList = quizzes.subList(i, Math.min(i + batchSize, quizzes.size()));
            jdbcTemplate.batchUpdate(
                    con -> {
                        PreparedStatement ps = con.prepareStatement(sql, new String[]{"quiz_id"});
                        return ps;
                    },
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Quiz newQuiz = batchList.get(i);
                            ps.setLong(1, newQuiz.getSong().getId());
                            if (newQuiz.getWord() != null) {
                                ps.setLong(2, newQuiz.getWord().getId());
                            } else {
                                ps.setNull(2, Types.BIGINT);
                            }
                            ps.setString(3, newQuiz.getType().toString());
                        }

                        @Override
                        public int getBatchSize() {
                            return batchList.size();
                        }
                    },
                    keyHolder
            );
            List<Map<String, Object>> keyList = keyHolder.getKeyList();
            for (int j = 0; j < batchList.size(); j++) {
                Quiz quiz = batchList.get(j);
                Number key = (Number) keyList.get(j).get("GENERATED_KEY");
                quiz.initId(key.longValue());
            }
        }
        em.flush();
    }
}
