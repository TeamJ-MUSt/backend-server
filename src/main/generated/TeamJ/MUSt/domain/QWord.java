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

    public static final QWord word = new QWord("word");

    public final StringPath classOfWord = createString("classOfWord");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath jpPronunciation = createString("jpPronunciation");

    public final ListPath<Meaning, QMeaning> meaning = this.<Meaning, QMeaning>createList("meaning", Meaning.class, QMeaning.class, PathInits.DIRECT2);

    public final ListPath<MemberWord, QMemberWord> memberWords = this.<MemberWord, QMemberWord>createList("memberWords", MemberWord.class, QMemberWord.class, PathInits.DIRECT2);

    public final ListPath<SongWord, QSongWord> songWords = this.<SongWord, QSongWord>createList("songWords", SongWord.class, QSongWord.class, PathInits.DIRECT2);

    public final StringPath spelling = createString("spelling");

    public QWord(String variable) {
        super(Word.class, forVariable(variable));
    }

    public QWord(Path<? extends Word> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWord(PathMetadata metadata) {
        super(Word.class, metadata);
    }

}

