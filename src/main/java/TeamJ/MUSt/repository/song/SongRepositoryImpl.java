package TeamJ.MUSt.repository.song;

import TeamJ.MUSt.domain.Song;
import TeamJ.MUSt.domain.Word;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static TeamJ.MUSt.domain.QMemberSong.memberSong;
import static TeamJ.MUSt.domain.QSong.song;
import static TeamJ.MUSt.domain.QSongWord.songWord;
import static com.querydsl.jpa.JPAExpressions.select;

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

    @Override
    public List<Tuple> findWithMemberSongCheckingRegister(Long memberId, String title, String artist) {
        return queryFactory
                .selectDistinct(
                        song,
                        song.in(
                                select(memberSong.song)
                                        .from(memberSong)
                                        .where(memberSong.member.id.eq(memberId))
                        )
                )
                .from(song)
                .where(titleEq(title), artistEq(artist))
                .fetch();
    }

    @Override
    public List<Word> findWithSongWordById(Long songId) {
        return queryFactory
                .select(songWord.word)
                .from(song, songWord).fetchJoin()
                .where(song.id.eq(songId))
                .fetch();
    }


    @Override
    public List<Song> findRequestSong(String title, String artist) {
        return queryFactory
                .select(song)
                .from(song)
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
