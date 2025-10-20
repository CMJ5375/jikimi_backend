package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardCategory is a Querydsl query type for BoardCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardCategory extends EntityPathBase<BoardCategory> {

    private static final long serialVersionUID = -306859440L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardCategory boardCategory = new QBoardCategory("boardCategory");

    public final QBoard board;

    public final NumberPath<Long> categoryId = createNumber("categoryId", Long.class);

    public final StringPath description = createString("description");

    public final StringPath name = createString("name");

    public QBoardCategory(String variable) {
        this(BoardCategory.class, forVariable(variable), INITS);
    }

    public QBoardCategory(Path<? extends BoardCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardCategory(PathMetadata metadata, PathInits inits) {
        this(BoardCategory.class, metadata, inits);
    }

    public QBoardCategory(Class<? extends BoardCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new QBoard(forProperty("board")) : null;
    }

}

