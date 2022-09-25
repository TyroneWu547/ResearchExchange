package research_exchange.repositories;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.jpa.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;
import research_exchange.models.GeneralComment;

/**
 * The GeneralComment repository class which handles the necessary queries
 * related to the GeneralComment entity.
 * 
 * @author Tyrone Wu
 */
@JdbcRepository(dialect = Dialect.ORACLE)
public interface GeneralCommentRepository
        extends CrudRepository<GeneralComment, Long>, JpaSpecificationExecutor<GeneralComment> {

    /**
     * Get all GeneralComments in the database.
     *
     * @return list of all GeneralComments
     */
    @Override
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    @Join(value = "commentPost.article", type = Join.Type.FETCH)
    List<GeneralComment> findAll();

    /**
     * Gets the GeneralComment by their ID.
     *
     * @param id the GeneralComment ID to filter by
     * @return the GeneralComment with the matching ID
     */
    @Override
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    @Join(value = "commentPost.article", type = Join.Type.FETCH)
    Optional<GeneralComment> findById(Long id);

    /**
     * Gets the GeneralComment by their comment post ID.
     *
     * @param commentPostId the comment post ID to filter by
     * @return the GeneralComment with the matching comment post comment post ID
     */
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    @Join(value = "commentPost.article", type = Join.Type.FETCH)
    Optional<GeneralComment> findByCommentPostId(Long commentPostId);

    /**
     * Get all top-level GeneralComment of an Article post.
     *
     * @param articleId the Article ID to filter by
     * @return list of all the top-level GeneralComment of the Article
     */
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    @Join(value = "commentPost.article", type = Join.Type.FETCH)
    List<GeneralComment> findAllByRootThreadIdIsNullAnd_CommentPost_ArticleId(Long articleId);

    /**
     * Get all followup GeneralComments of a GeneralComment thread.
     *
     * @param rootThreadId the GeneralComment thread ID to filter by
     * @return list of all followup GeneralComments of a GeneralComment thread
     */
    @NonNull
    @Join(value = "commentPost", type = Join.Type.FETCH)
    @Join(value = "commentPost.user", type = Join.Type.FETCH)
    List<GeneralComment> findAllByRootThreadId(Long rootThreadId);

}
