package TeamJ.MUSt.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeaning is a Querydsl query type for Meaning
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeaning extends EntityPathBase<Meaning> {

    private static final long serialVersionUID = -1700501737L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeaning meaning1 = new QMeaning("meaning1");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath meaning = createString("meaning");

    public final QWord word;

    public QMeaning(String variable) {
        this(Meaning.class, forVariable(variable), INITS);
    }

    public QMeaning(Path<? extends Meaning> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeaning(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeaning(PathMetadata metadata, PathInits inits) {
        this(Meaning.class, metadata, inits);
    }

    public QMeaning(Class<? extends Meaning> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.word = inits.isInitialized("word") ? new QWord(forProperty("word"), inits.get("word")) : null;
    }

}

