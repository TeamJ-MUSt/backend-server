package TeamJ.MUSt.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberSong is a Querydsl query type for MemberSong
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberSong extends EntityPathBase<MemberSong> {

    private static final long serialVersionUID = 1054446453L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberSong memberSong = new QMemberSong("memberSong");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QSong song;

    public QMemberSong(String variable) {
        this(MemberSong.class, forVariable(variable), INITS);
    }

    public QMemberSong(Path<? extends MemberSong> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberSong(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberSong(PathMetadata metadata, PathInits inits) {
        this(MemberSong.class, metadata, inits);
    }

    public QMemberSong(Class<? extends MemberSong> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.song = inits.isInitialized("song") ? new QSong(forProperty("song")) : null;
    }

}

