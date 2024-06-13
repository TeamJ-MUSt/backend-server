package TeamJ.MUSt.repository.wordbook;

import TeamJ.MUSt.domain.MemberWord;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static TeamJ.MUSt.domain.QMemberWord.memberWord;
import static TeamJ.MUSt.domain.QSongWord.songWord;


public class MemberWordRepositoryImpl implements MemberWordRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberWordRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberWord> findWithMemberAndWord(Long memberId) {
        /*return  queryFactory
                .select(wordBook)
                .from(wordBook, wordBook.word, wordBook.member).fetchJoin()
                .where(wordBook.member.id.eq(memberId)).fetch();*/
        return null;
    }

    @Override
    public long deleteBySpellingAndMeaning(String spelling, String meaning) {
        /*return queryFactory
                .delete(wordBook)
                .where(wordBook.word.spelling.eq(spelling)
                        .and(wordBook.word.meaning.eq(meaning))).execute();*/
        return 1L;
    }

    @Override
    public long deleteMemberWordByMemberIdAndSongId(Long memberId, Long songId) {
        return queryFactory
                .delete(memberWord)
                .where(memberWord.word.in(
                        queryFactory
                                .select(songWord.word)
                                .from(songWord)
                                .where(songWord.song.id.eq(songId))
                )).execute();
    }


}
