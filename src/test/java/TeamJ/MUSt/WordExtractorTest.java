package TeamJ.MUSt;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WordExtractorTest {
    @Autowired
    WordExtractor wordExtractor;
    @Autowired
    SongRepository songRepository;

    @Test
    public void 단어_추출() throws Exception{
        List<WordInfo> wordInfos = wordExtractor.extractWords(1l);
        for (WordInfo wordInfo : wordInfos) {
            System.out.println(wordInfo);
        }
    }
    @Test
    public void 테스트() throws Exception{
        List<Song> songs = songRepository.findAll();
        for (Song song : songs) {
            System.out.println(song.getTitle());
        }
    }
}