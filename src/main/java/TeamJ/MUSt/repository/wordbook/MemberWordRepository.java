package TeamJ.MUSt.repository.wordbook;

import TeamJ.MUSt.domain.MemberWord;
import TeamJ.MUSt.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberWordRepository extends JpaRepository<MemberWord, Long>, MemberWordRepositoryCustom {
    /*@Query("select mw.word from MemberWord mw where mw.member.id = (:memberId)")
    List<Word> findWithWordByMemberId(@Param("memberId") Long memberId);*/
    @Query("select mw.word from MemberWord mw join mw.word w where mw.member.id = (:memberId)")
    List<Word> findWithWordByMemberId(@Param("memberId") Long memberId);
}
