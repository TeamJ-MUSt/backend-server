package TeamJ.MUSt.repository;

import TeamJ.MUSt.domain.Meaning;
import TeamJ.MUSt.domain.Word;
import com.querydsl.core.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByMemberId(Long memberId);
    Word findBySpelling(String spelling);

    @Query("select w.meaning from Word w join w.meaning m where w.id in (:ids)")
    List<Meaning> findRandomMeaning(@Param("ids") List<Long> ids);

    @Query("select w from Word w join fetch w.songWords sw where sw.song.id = :id")
    List<Word> findWithSong(@Param("id") Long id);

    @Query("select w from Word w where w.id in (:ids)")
    List<Word> findInIds(@Param("ids") List<Long> ids);
    @Override
    long count();

    //@Query("select w from Word w where w.classOfWord = (:field)")
    List<Word> findByClassOfWord(@Param("field") String field);

    @Query(nativeQuery = true,value = "select * from word where spelling not REGEXP '^[\\x{3040}-\\x{309F}]$'")
    List<Word> findWithoutHiragana();
}
