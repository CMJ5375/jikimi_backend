package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJUser is a Querydsl query type for JUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJUser extends EntityPathBase<JUser> {

    private static final long serialVersionUID = -1603072095L;

    public static final QJUser jUser = new QJUser("jUser");

    public final StringPath address = createString("address");

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final StringPath email = createString("email");

    public final ListPath<JMemberRole, EnumPath<JMemberRole>> JMemberRoleList = this.<JMemberRole, EnumPath<JMemberRole>>createList("JMemberRoleList", JMemberRole.class, EnumPath.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath socialType = createString("socialType");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final StringPath username = createString("username");

    public QJUser(String variable) {
        super(JUser.class, forVariable(variable));
    }

    public QJUser(Path<? extends JUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJUser(PathMetadata metadata) {
        super(JUser.class, metadata);
    }

}

