package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.Word;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class WordServiceTest {
    @Autowired
    WordService wordService;

    @Test
    public void 단어_파일에_저장() throws Exception{
        wordService.writeWordList();
    }
}