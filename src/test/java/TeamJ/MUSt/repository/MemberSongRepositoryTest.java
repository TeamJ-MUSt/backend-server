package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Member;
import TeamJ.MUSt.domain.MemberSong;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberSongRepositoryTest {
    @Autowired MemberSongRepository memberSongRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    void 목록에서_노래_삭제() throws InterruptedException {
        memberSongRepository.deleteByMemberIdAndSongId(1l, 1l);
        em.flush();
        em.clear();
        Member findMember = memberRepository.findById(1l).get();
        for (MemberSong memberSong : findMember.getMemberSongs()){
            if(memberSong.getSong().getId() == 1)
                throw new RuntimeException();
        }
    }
}