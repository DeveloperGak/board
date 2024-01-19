package site.board.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCrew is a Querydsl query type for Crew
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCrew extends EntityPathBase<Crew> {

    private static final long serialVersionUID = -448117124L;

    public static final QCrew crew = new QCrew("crew");

    public final StringPath id = createString("id");

    public final StringPath password = createString("password");

    public final EnumPath<CrewRole> role = createEnum("role", CrewRole.class);

    public QCrew(String variable) {
        super(Crew.class, forVariable(variable));
    }

    public QCrew(Path<? extends Crew> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCrew(PathMetadata metadata) {
        super(Crew.class, metadata);
    }

}

