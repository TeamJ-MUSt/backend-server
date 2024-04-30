package TeamJ.MUSt.repository.wordbook;

import TeamJ.MUSt.domain.WordBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordBookRepository extends JpaRepository<WordBook, Long>, WordBookRepositoryCustom{
    List<WordBook> findByMemberId(Long memberId);
}
