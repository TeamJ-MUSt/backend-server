package TeamJ.MUSt.repository.word;

import TeamJ.MUSt.domain.Word;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class WordRepositoryImpl implements WordRepositoryCustom{
    public static final String INSERT_QUERY = "INSERT INTO word (class_of_word, jp_pronunciation, spelling) VALUES (? ,? ,?)";
    public static final int BATCH_SIZE = 1000;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    public WordRepositoryImpl(DataSource dataSource, EntityManager entityManager){
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }
    @Transactional
    @Override
    public List<Word> bulkSave(List<Word> words) {

        for (int i = 0; i < words.size(); i += BATCH_SIZE) {

            List<Word> batchList = getBatchingList(words, i);
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.batchUpdate(
                    con -> {
                        PreparedStatement ps = con.prepareStatement(INSERT_QUERY, new String[]{"word_id"});
                        return ps;
                    },
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Word newWord = batchList.get(i);
                            ps.setString(1, newWord.getClassOfWord());
                            ps.setString(2, newWord.getJpPronunciation());
                            ps.setString(3, newWord.getSpelling());
                        }

                        @Override
                        public int getBatchSize() {
                            return batchList.size();
                        }
                    },
                    keyHolder
            );

            List<Map<String, Object>> generatedKeys = keyHolder.getKeyList();

            for (int j = 0; j < batchList.size(); j++) {
                Word word = batchList.get(j);
                Long key = getGeneratedKey(generatedKeys, j);
                word.updateId(key);
            }
        }

        em.flush();
        return words;
    }

    private static List<Word> getBatchingList(List<Word> words, int start) {
        int end = Math.min(start + BATCH_SIZE, words.size());
        return words.subList(start, end);
    }

    private static Long getGeneratedKey(List<Map<String, Object>> generatedKeys, int index) {
        Number key = (Number) generatedKeys.get(index).get("GENERATED_KEY");
        return key.longValue();
    }
}
