package TeamJ.MUSt.util;

import TeamJ.MUSt.domain.Meaning;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.domain.Word;
import TeamJ.MUSt.repository.WordRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class NlpModuleTest {
    NlpModule module = new NlpModule();

    @Autowired
    WordRepository wordRepository;

    @Autowired
    SongRepository songRepository;
    @Test
    public void 동작_테스트() throws Exception{
        String test = "空にある何かを見つめてたら@{#word#:#何#, #definitions#:[#일정하지 않은 것을 가리키는 대명사: 무엇.#, #아어 왜, 어째서, 무엇 때문에.#, #어째서#, #아니#, #어느 날#, #(내가 $아니$모르는) 무엇#, #어떤 것#]}";
        //module.reflectContext(test);
        Word word = wordRepository.findById(1l).get();
        Song song = songRepository.findById(1l).get();
        System.out.println(song.getLyric());
    }
}