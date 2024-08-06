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
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;

    public WordRepositoryImpl(DataSource dataSource, EntityManager entityManager){
        jdbcTemplate = new JdbcTemplate(dataSource);
        em = entityManager;
    }
    @Transactional
    @Override
    public List<Word> bulkSaveWord(List<Word> words) {
        String sql = "INSERT INTO word (class_of_word, jp_pronunciation, spelling) VALUES (? ,? ,?)";
        int batchSize = 1000;

        for (int i = 0; i < words.size(); i += batchSize) {
            List<Word> batchList = words.subList(i, Math.min(i + batchSize, words.size()));
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.batchUpdate(
                    con -> {
                        PreparedStatement ps = con.prepareStatement(sql, new String[]{"word_id"});
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

            List<Map<String, Object>> keyList = keyHolder.getKeyList();
            for (int j = 0; j < batchList.size(); j++) {
                Word word = batchList.get(j);
                Number key = (Number) keyList.get(j).get("GENERATED_KEY");
                word.updateId(key.longValue());
            }
        }

        em.flush();
        return words;
    }
}
