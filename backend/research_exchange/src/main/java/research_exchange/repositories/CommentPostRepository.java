package research_exchange.repositories;

import research_exchange.models.CommentPost;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

/**
 * The CommentPost repository class which handles the necessary queries related
 * to the CommentPost entity.
 * 
 * @author Tyrone Wu
 */
@JdbcRepository(dialect = Dialect.ORACLE)
public interface CommentPostRepository extends CrudRepository<CommentPost, Long> {

    /**
     * Gets the CommentPost by their ID.
     *
     * @param id the CommentPost ID to filter by
     * @return the CommentPost with the matching ID
     */
    @Override
    Optional<CommentPost> findById(Long id);

    /**
     * Check if CommentPost exists in the Article.
     *
     * @param id        the CommentPost ID to filter by
     * @param articleId the Article ID to filter by
     * @return true if CommentPost exists in the Article; otherwise, return false
     */
    boolean existsByIdAndArticleId(Long id, Long articleId);

    /**
     * Update CommentPost score by incrementing/decrementing it.
     *
     * @param id    the CommentPost ID to filter by
     * @param score the new score to update with
     */
    void update(@Id Long id, Integer score);

}
