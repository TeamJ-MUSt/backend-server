package TeamJ.MUSt.repository.wordbook;

import TeamJ.MUSt.domain.WordBook;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static TeamJ.MUSt.domain.QWordBook.*;

public class WordBookRepositoryImpl implements WordBookRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public WordBookRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<WordBook> findWithMemberAndWord(Long memberId) {
        return  queryFactory
                .select(wordBook)
                .from(wordBook, wordBook.word, wordBook.member).fetchJoin()
                .where(wordBook.member.id.eq(memberId)).fetch();
    }

    @Override
    public long deleteBySpellingAndMeaning(String spelling, String meaning) {
        return queryFactory
                .delete(wordBook)
                .where(wordBook.word.spelling.eq(spelling)
                        .and(wordBook.word.meaning.eq(meaning))).execute();
    }


}
