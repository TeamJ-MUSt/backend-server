package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Meaning;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.meaning.MeaningRepositoryImpl;
import TeamJ.MUSt.repository.word.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
class MeaningRepositoryTest {
    @Autowired
    MeaningRepositoryImpl meaningRepository;

    @Autowired
    WordRepository wordRepository;
    @Test
    @Commit
    public void 벌크_삽입() throws Exception{
        List<Word> words = new ArrayList<>();
        for(int i = 0; i < 100; i++)
            words.add(new Word("" + i, "s", null, "명사"));
        words = wordRepository.bulkSave(words);

        List<Meaning> meanings = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            meanings.add(new Meaning("" + i, words.get(i)));
        }
        long start = System.currentTimeMillis();
        meaningRepository.bulkSave(meanings);
        long end = System.currentTimeMillis();
        log.info("걸린 시간 {}ms", end - start);
        for (Word word : words) {
            Assertions.assertThat(word.getId()).isNotNull();
        }
    }

}