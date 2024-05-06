package TeamJ.MUSt.service;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.repository.MemberRepository;
import TeamJ.MUSt.repository.song.SongRepository;
import TeamJ.MUSt.service.song.SongService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class SongServiceTest {
    @Autowired
    EntityManager em;
    @Autowired
    SongService songService;
    @Autowired
    SongRepository songRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void 유저의_모든_노래_조회() {
        Member member1 = memberRepository.findById(1L).get();
        Member member2 = memberRepository.findById(2L).get();

        List<Song> userSong1 = songService.findUserSong(member1.getId());
        assertThat(userSong1.size()).isEqualTo(8);
        for (int i = 0; i < userSong1.size(); i++)
            assertThat(userSong1.get(i).getId()).isEqualTo(i + 1);

        List<Song> userSong2 = songService.findUserSong(member2.getId());
        assertThat(userSong2.size()).isEqualTo(6);
        for (int i = 0; i < userSong2.size(); i++)
            assertThat(userSong2.get(i).getId()).isEqualTo(i + 3);
    }

    @Test
    void 페이지1_조회() {
        Member member1 = memberRepository.findById(1L).get();

        List<Song> userSong1 = songService.findUserSong(member1.getId(), PageRequest.of(0, 4));
        assertThat(userSong1.size()).isEqualTo(4);
        for (int i = 0; i < userSong1.size(); i++)
            assertThat(userSong1.get(i).getId()).isEqualTo(i + 1);
    }

    @Test
    void 페이지2_조회() {
        Member member1 = memberRepository.findById(1L).get();

        List<Song> userSong1 = songService.findUserSong(member1.getId(), PageRequest.of(1, 4));
        assertThat(userSong1.size()).isEqualTo(4);
        for (int i = 0; i < userSong1.size(); i++)
            assertThat(userSong1.get(i).getId()).isEqualTo(i + 5);
    }

    @Test
    public void 페이지_크기보다_원소_수가_적음() throws Exception {
        Member member2 = memberRepository.findById(2L).get();

        List<Song> userSong1 = songService.findUserSong(member2.getId(), PageRequest.of(1, 6));
        assertThat(userSong1.size()).isEqualTo(0);
    }

    @Test
    public void 등록_노래중_검색결과가_있음() throws Exception{
        List<Song> songs = songService.searchMySong(1l, null, "yuuri");
        assertThat(songs.size()).isEqualTo(2);
        assertThat(songs.get(0).getTitle()).isEqualTo("BETELGEUSE");
        assertThat(songs.get(0).getArtist()).isEqualTo("Yuuri");
        assertThat(songs.get(1).getTitle()).isEqualTo("Leo");
        assertThat(songs.get(1).getArtist()).isEqualTo("Yuuri");

    }
    @Test
    public void 검색한_노래가_등록한_노래가_아님() throws Exception{
        List<Song> songs = songService.searchDbSong("sparkle", "radwimps");
        assertThat(songs.size()).isEqualTo(4);
    }

    @Test
    public void 노래_등록() throws Exception{
        List<Song> before = songService.findUserSong(1l);
        songService.registerSong(1l, 12l);
        List<Song> after = songService.findUserSong(1l);
        assertThat(after.size() - before.size()).isEqualTo(1);
    }
}