package research_exchange.repositories;

import research_exchange.models.ExpertReview;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

/**
 * The Expert Review repository class which handles the necessary queries
 * related to the Expert Review entity.
 * 
 * @author Tyrone Wu
 */
@JdbcRepository(dialect = Dialect.ORACLE)
public interface ExpertReviewRepository extends CrudRepository<ExpertReview, Long> {

    /**
     * Gets all the Expert Reviews in the database.
     *
     * @return list of all the Expert Reviews
     */
    @Override
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    @Join(value = "commentPost.article", type = Join.Type.FETCH)
    List<ExpertReview> findAll();

    /**
     * Gets the Expert Review by their ID.
     *
     * @param commentPostId the Expert Review ID to filter by
     * @return the Expert Review with the matching ID
     */
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    @Join(value = "commentPost.article", type = Join.Type.FETCH)
    Optional<ExpertReview> findByCommentPostId(Long commentPostId);

    /**
     * Get all the Expert Reviews of the Article post.
     * 
     * @param articleId the Article ID to filter by
     * @return list of all the Expert Reviews associated with the Article
     */
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    @Join(value = "commentPost.article", type = Join.Type.FETCH)
    List<ExpertReview> findAllByIdIsNotNullAnd_CommentPost_ArticleId(Long articleId);

    /**
     * Get all Expert Reviews made by the User.
     *
     * @param userId the User ID to filter by
     * @return list of all Expert Reviews made by the User
     */
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    @Join(value = "commentPost.article", type = Join.Type.FETCH)
    List<ExpertReview> findAllByCommentPostUserId(Long userId);

    /**
     * Check if the Expert Review exists by User ID and Article ID.
     * 
     * @param userId    the User ID to filter by
     * @param articleId the Article ID to filter by
     * @return true if Expert Review exists with the matching User and Article ID;
     *         otherwise, return false
     */
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    @Join(value = "commentPost.article", type = Join.Type.FETCH)
    boolean existsByIdIsNotNullAnd_CommentPost_UserIdAnd_CommentPost_ArticleId(Long userId, Long articleId);

    /**
     * Update the status of an Expert Review by their ID.
     *
     * @param commentPostId the Expert Review ID to filter by
     * @param status        the new status to update
     */
    void updateByCommentPostId(Long commentPostId, String status);

}
