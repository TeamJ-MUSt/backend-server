package TeamJ.MUSt.repository.word;

import TeamJ.MUSt.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long>, WordRepositoryCustom {

    Word findBySpelling(String spelling);


    @Query("select w from Word w join fetch w.songWords sw where sw.song.id = :id")
    List<Word> findWithSong(@Param("id") Long id);

    @Override
    long count();

    List<Word> findByClassOfWord(@Param("field") String field);

    @Query(nativeQuery = true, value = "SELECT * FROM word WHERE spelling NOT REGEXP '^[\\u3040-\\u309F]$'")
    List<Word> findWithoutHiragana();

    @Query("select w from Word w join fetch w.meaning")
    List<Word> findWithMeaning();
}
