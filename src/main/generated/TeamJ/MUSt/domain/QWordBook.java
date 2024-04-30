package TeamJ.MUSt.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWordBook is a Querydsl query type for WordBook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWordBook extends EntityPathBase<WordBook> {

    private static final long serialVersionUID = -166310087L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWordBook wordBook = new QWordBook("wordBook");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QWord word;

    public QWordBook(String variable) {
        this(WordBook.class, forVariable(variable), INITS);
    }

    public QWordBook(Path<? extends WordBook> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWordBook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWordBook(PathMetadata metadata, PathInits inits) {
        this(WordBook.class, metadata, inits);
    }

    public QWordBook(Class<? extends WordBook> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.word = inits.isInitialized("word") ? new QWord(forProperty("word"), inits.get("word")) : null;
    }

}

