package research_exchange.models;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;

/**
 * The Expert Review entity class which has a foreign key reference to User and
 * Article.
 * 
 * @author Tyrone Wu
 */
@MappedEntity("EXPERT_REVIEW")
public class ExpertReview {

    /** Unique ID for Expert Review entity. */
    @Id
    @GeneratedValue
    @MappedProperty("ID")
    private Long id;

    /** Foreign key to CommentPost. */
    @Relation(value = Kind.ONE_TO_ONE, cascade = Relation.Cascade.ALL)
    @MappedProperty("COMMENT_POST_ID")
    private CommentPost commentPost;

    /** The status that the Expert Review assigns to the Article. */
    @MappedProperty("STATUS")
    private String status;

    /**
     * Constructor for creating Expert Review on an Article. The date, score, and
     * foreign keys to User and Article are automatically set.
     * 
     * @param commentPost the abstract CommentPost to set
     * @param status      the status of the Expert Review to set
     */
    @Creator
    public ExpertReview(@Nullable CommentPost commentPost, String status) {
        this.commentPost = commentPost;
        this.status = status;
    }

    /**
     * Gets unique ID of the Expert Review.
     *
     * @return the ID of the Expert Review as Long
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique ID of the Expert Review.
     *
     * @param id the unique ID of Expert Review to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the abstract CommentPost of the Expert Review.
     *
     * @return the abstract CommentPost of the Expert Review
     */
    public CommentPost getCommentPost() {
        return commentPost;
    }

    /**
     * Gets the status that the Expert Review assigned.
     *
     * @return the status that the Expert Review assigned
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
