package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.domain.MemberWord;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.WordRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.util.WordInfo;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class MemberWordServiceTest {
    @Autowired
    MemberWordService memberWordService;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    EntityManager em;
    @Test
    public void 퀴즈를_풀고_등록() throws Exception{
        boolean register = memberWordService.register(1l, 1l);
        List<Word> userWord = memberWordService.findUserWord(1l);
        List<Word> songWord = wordRepository.findWithSong(1l);
        assertThat(userWord.size()).isEqualTo(songWord.size());
        for(int i = 0; i < userWord.size(); i++)
            assertThat(userWord.get(i).getSpelling()).isEqualTo(songWord.get(i).getSpelling());

    }

    @Test
    public void 유저가_노래를_지워_학습_단어가_삭제() throws Exception{
        memberWordService.register(1l, 1l);
        memberWordService.deleteWord(1l, 1l);
        List<Word> userWord = memberWordService.findUserWord(1l);
        assertThat(userWord.size()).isEqualTo(0);
    }

    @Test
    public void 비슷한_단어() throws Exception{
    }
}