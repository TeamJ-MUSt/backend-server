package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.repository.song.SongRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class SongRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    SongRepository songRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 노래등록() throws Exception {
        Song song = new Song();
        Song savedSong = songRepository.save(song);
        Optional<Song> findSong = songRepository.findById(savedSong.getId());
        assertThat(findSong.get()).isEqualTo(savedSong);
    }

    @Test
    public void 요청_노래_찾기_제목과_가수() throws Exception {
        List<Song> requestSong = songRepository.findRequestSong(1l, "BETELGEUSE", "Yuuri");
        assertThat(requestSong.size()).isEqualTo(1);
        assertThat(requestSong.get(0).getTitle()).isEqualTo("BETELGEUSE");
        assertThat(requestSong.get(0).getArtist()).isEqualTo("Yuuri");
    }

    @Test
    public void 요청_노래_찾기_제목만() throws Exception {
        List<Song> requestSong = songRepository.findRequestSong(1l, "BETELGEUSE", null);
        assertThat(requestSong.size()).isEqualTo(2);
        assertThat(requestSong.get(0).getArtist()).isEqualTo("Yuuri");
        assertThat(requestSong.get(1).getArtist()).isEqualTo("KSUKE");
    }

    @Test
    public void 요청_노래_찾기_가수만() throws Exception {
        List<Song> requestSong = songRepository.findRequestSong(1l, null, "Yuuri");
        assertThat(requestSong.size()).isEqualTo(2);
        assertThat(requestSong.get(0).getTitle()).isEqualTo("BETELGEUSE");
        assertThat(requestSong.get(0).getArtist()).isEqualTo("Yuuri");
        assertThat(requestSong.get(1).getTitle()).isEqualTo("Leo");
        assertThat(requestSong.get(1).getArtist()).isEqualTo("Yuuri");
    }
    @Test
    public void 제목_가수로_대소문자_구분하지_않고_조회() throws Exception{
        List<Song> songs = songRepository.findRequestSong(1l, "betelgeuse", "yuuri");
        assertThat(songs.size()).isEqualTo(1);
        assertThat(songs.get(0).getId()).isEqualTo(1);
    }
    @Test
    public void 제목만으로_대소문자_구분하지_않고_조회() throws Exception{
        List<Song> songs = songRepository.findRequestSong(1l, "betelgeuse", null);
        assertThat(songs.size()).isEqualTo(2);
        assertThat(songs.get(0).getId()).isEqualTo(1);
        assertThat(songs.get(1).getId()).isEqualTo(7);
    }
    @Test
    public void 가수만으로_대소문자_구분하지_않고_조회() throws Exception{
        List<Song> songs = songRepository.findRequestSong(1l, null, "yuuri");
        assertThat(songs.size()).isEqualTo(2);
        assertThat(songs.get(0).getId()).isEqualTo(1);
        assertThat(songs.get(1).getId()).isEqualTo(6);
    }
}
