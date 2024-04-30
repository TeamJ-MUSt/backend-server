package TeamJ.MUSt.repository.song;

import TeamJ.MUSt.domain.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static TeamJ.MUSt.domain.QMemberSong.*;
import static TeamJ.MUSt.domain.QSong.song;
import static TeamJ.MUSt.domain.QSongWord.songWord;

public class SongRepositoryImpl implements SongRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public SongRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    public List<Song> findWithMemberSong(Long memberId){
        return queryFactory
                .selectDistinct(song)
                .from(song)
                .join(song.memberSongs, memberSong)
                .fetchJoin()
                .where(memberSong.member.id.eq(memberId))
                .fetch();
    }

    public List<Song> findWithMemberSong(Long memberId, Pageable pageable){
        return queryFactory
                .selectDistinct(song)
                .from(song)
                .join(song.memberSongs, memberSong)
                .fetchJoin()
                .where(memberSong.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
    @Override
    public List<Word> findUsedWords(Long songId) {
        return queryFactory
                .select(songWord.word)
                .from(song, songWord).fetchJoin()
                .where(song.id.eq(songId))
                .fetch();
    }

    @Override
    public List<Song> findRequestSong(Long memberId, String title, String artist) {
        return queryFactory
                .selectDistinct(song)
                .from(song)
                .join(song.memberSongs, memberSong)
                .fetchJoin()
                .where(memberSong.member.id.eq(memberId))
                .where(titleEq(title), artistEq(artist))
                .fetch();
    }

    @Override
    public int countSearchResult(String title, String artist) {
        return queryFactory
                .select(song.title, song.artist, song.lyric)
                .from(song)
                .where(titleEq(title), artistEq(artist))
                .fetch().size();
    }

    private BooleanExpression titleEq(String titleCond){
        return titleCond == null ? null : song.title.toUpperCase().like("%"+titleCond.toUpperCase()+"%");
    }
    private BooleanExpression artistEq(String artistCond){
        return artistCond == null ? null : song.artist.toUpperCase().eq(artistCond.toUpperCase());
    }
}
