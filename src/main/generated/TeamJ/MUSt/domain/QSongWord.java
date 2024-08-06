package TeamJ.MUSt.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSongWord is a Querydsl query type for SongWord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSongWord extends EntityPathBase<SongWord> {

    private static final long serialVersionUID = 1341262821L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSongWord songWord = new QSongWord("songWord");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSong song;

    public final StringPath surface = createString("surface");

    public final QWord word;

    public QSongWord(String variable) {
        this(SongWord.class, forVariable(variable), INITS);
    }

    public QSongWord(Path<? extends SongWord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSongWord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSongWord(PathMetadata metadata, PathInits inits) {
        this(SongWord.class, metadata, inits);
    }

    public QSongWord(Class<? extends SongWord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.song = inits.isInitialized("song") ? new QSong(forProperty("song")) : null;
        this.word = inits.isInitialized("word") ? new QWord(forProperty("word")) : null;
    }

}

