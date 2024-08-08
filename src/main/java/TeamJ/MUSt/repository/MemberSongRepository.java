package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.MemberSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MemberSongRepository extends JpaRepository<MemberSong, Long> {
    @Transactional
    int deleteByMemberIdAndSongId(Long memberId, Long songId);
}
