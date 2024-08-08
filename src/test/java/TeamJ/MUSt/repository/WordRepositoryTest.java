package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.word.WordRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
public class WordRepositoryTest {
    @Autowired
    WordRepository wordRepository;

    @Autowired
    EntityManager em;

    @Test
    public void 벌크_삽입_테스트() throws Exception{
        List<Word> words = new ArrayList<>();
        for(int i = 0; i < 309; i++)
            words.add(new Word("" + i, "s", null, "명사"));
        long start = System.currentTimeMillis();
        wordRepository.bulkSave(words);
        long end = System.currentTimeMillis();
        log.info("걸린 시간 {}ms", end - start);
    }
    @Test
    public void 기존_삽입() throws Exception{
        List<Word> words = new ArrayList<>();
        for(int i = 0; i < 124; i++){
            words.add(new Word("" + i, "s", null, "명사"));
        }
        long start = System.currentTimeMillis();
        for (Word word : words)
            wordRepository.save(word);

        long end = System.currentTimeMillis();
        log.info("걸린 시간 {}ms", end - start);
    }

    @Test
    public void 테스트() throws Exception{
    }
}
