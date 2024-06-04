package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUsernameAndPassword(String id, String password);
}
