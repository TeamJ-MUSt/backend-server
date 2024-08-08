package TeamJ.MUSt.service;

import TeamJ.MUSt.repository.MemberSongRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberSongService {
    private final MemberSongRepository memberSongRepository;
    private final EntityManager em;

    @Transactional
    public int deleteSongInList(Long memberId, Long songId){
        int deletedNum = memberSongRepository.deleteByMemberIdAndSongId(memberId, songId);
        em.flush();
        em.clear();

        return deletedNum;
    }
}
