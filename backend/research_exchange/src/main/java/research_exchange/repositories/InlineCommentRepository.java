package research_exchange.repositories;

import research_exchange.models.InlineComment;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

/**
 * The Inline Comment repository class which handles the necessary queries
 * related to the InComment entity.
 * 
 * @author Tyrone Wu
 */
@JdbcRepository(dialect = Dialect.ORACLE)
public interface InlineCommentRepository extends CrudRepository<InlineComment, Long> {

    /**
     * Get all the Inline Comments of the Expert Review.
     * 
     * @param expertReviewId the Expert Review ID to filter by
     * @return list of all the Inline Comments associated with the Expert Review
     */
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    @Join(value = "commentPost.article", type = Join.Type.FETCH)
    @Join(value = "highlightSections", type = Join.Type.FETCH)
    List<InlineComment> findAllByExpertReviewId(Long expertReviewId);

}
