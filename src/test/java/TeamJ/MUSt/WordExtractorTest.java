package TeamJ.MUSt;

import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.util.WordExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class WordExtractorTest {
    @Autowired
    WordExtractor wordExtractor;
    @Autowired
    SongRepository songRepository;

    @Test
    public void 단어_추출() throws Exception{
        //List<WordInfo> wordInfos = wordExtractor.extractWords(1l);
        //System.out.println(wordInfos.get(0).getLemma());
    }
    @Test
    public void 테스트() throws Exception{
        List<Song> songs = songRepository.findAll();
        for (Song song : songs) {
            System.out.println(song.getTitle());
        }
    }
}