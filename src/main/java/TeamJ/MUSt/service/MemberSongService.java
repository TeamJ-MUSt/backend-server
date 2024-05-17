package TeamJ.MUSt.service;

import TeamJ.MUSt.repository.MemberSongRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberSongService {
    @Autowired
    MemberSongRepository memberSongRepository;

    @Autowired
    EntityManager em;

    @Transactional
    public void deleteSongInList(Long memberId, Long songId){
        memberSongRepository.deleteByMemberIdAndSongId(memberId, songId);
        em.flush();
        em.clear();
    }
}
