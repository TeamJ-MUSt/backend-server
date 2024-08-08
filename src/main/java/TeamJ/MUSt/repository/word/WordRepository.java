package TeamJ.MUSt.repository.word;

import TeamJ.MUSt.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long>, WordRepositoryCustom {

    String HIRAGANA_UNICODE_RANGE = "'^[\\u3040-\\u309F]$'";

    Word findBySpelling(String spelling);


    //이거 컬렉션 페치 조인이라 위험하다 수정 예정
    @Query("select w from Word w join fetch w.songWords sw where sw.song.id = :id")
    List<Word> findWithSong(@Param("id") Long id);

    @Override
    long count();

    List<Word> findByClassOfWord(@Param("classOfWord") String classOfWord);

    @Query(nativeQuery = true, value = "select * from word where spelling not regexp " + HIRAGANA_UNICODE_RANGE)
    List<Word> findWithoutHiragana();

    //여기도 컬렉션 페치 조인이라 위험하다
    @Query("select w from Word w join fetch w.meaning")
    List<Word> findWithMeaning();
}
