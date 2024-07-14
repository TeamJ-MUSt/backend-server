package TeamJ.MUSt.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWord is a Querydsl query type for Word
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWord extends EntityPathBase<Word> {

    private static final long serialVersionUID = 623786032L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWord word = new QWord("word");

    public final StringPath classOfWord = createString("classOfWord");

    public final StringPath enPronunciation = createString("enPronunciation");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath jpPronunciation = createString("jpPronunciation");

    public final ListPath<Meaning, QMeaning> meaning = this.<Meaning, QMeaning>createList("meaning", Meaning.class, QMeaning.class, PathInits.DIRECT2);

    public final QMember member;

    public final ListPath<MemberWord, QMemberWord> memberWords = this.<MemberWord, QMemberWord>createList("memberWords", MemberWord.class, QMemberWord.class, PathInits.DIRECT2);

    public final ListPath<SongWord, QSongWord> songWords = this.<SongWord, QSongWord>createList("songWords", SongWord.class, QSongWord.class, PathInits.DIRECT2);

    public final StringPath spelling = createString("spelling");

    public QWord(String variable) {
        this(Word.class, forVariable(variable), INITS);
    }

    public QWord(Path<? extends Word> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWord(PathMetadata metadata, PathInits inits) {
        this(Word.class, metadata, inits);
    }

    public QWord(Class<? extends Word> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

