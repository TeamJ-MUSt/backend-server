package TeamJ.MUSt.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSong is a Querydsl query type for Song
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSong extends EntityPathBase<Song> {

    private static final long serialVersionUID = 623666747L;

    public static final QSong song = new QSong("song");

    public final StringPath artist = createString("artist");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final ArrayPath<char[], Character> lyric = createArray("lyric", char[].class);

    public final ListPath<MemberSong, QMemberSong> memberSongs = this.<MemberSong, QMemberSong>createList("memberSongs", MemberSong.class, QMemberSong.class, PathInits.DIRECT2);

    public final ListPath<SongWord, QSongWord> songWords = this.<SongWord, QSongWord>createList("songWords", SongWord.class, QSongWord.class, PathInits.DIRECT2);

    public final ArrayPath<byte[], Byte> thumbnail = createArray("thumbnail", byte[].class);

    public final StringPath title = createString("title");

    public QSong(String variable) {
        super(Song.class, forVariable(variable));
    }

    public QSong(Path<? extends Song> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSong(PathMetadata metadata) {
        super(Song.class, metadata);
    }

}

