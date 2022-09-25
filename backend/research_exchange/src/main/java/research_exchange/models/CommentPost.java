package research_exchange.models;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;
import java.time.Instant;

/**
 * The Comment Post entity class that represents an abstract post of a comment.
 * This has a foreign key to Article and User.
 * 
 * @author Tyrone Wu
 */
@MappedEntity("COMMENT_POST")
public class CommentPost {

    /** Unique ID for CommentPost entity. */
    @Id
    @GeneratedValue
    @MappedProperty("ID")
    private Long id;

    /** Foreign key to User that posted the CommentPost. */
    @Nullable
    @Relation(Kind.MANY_TO_ONE)
    @MappedProperty("USER_ID")
    private User user;

    /** Foreign key to Article entity that the CommentPost is on. */
    @Nullable
    @Relation(Kind.MANY_TO_ONE)
    @MappedProperty("ARTICLE_ID")
    private Article article;

    /** The CommentPost's net score. */
    @MappedProperty("SCORE")
    private Integer score;

    /** Date the CommentPost was posted. */
    @DateCreated
    @MappedProperty("DATE_POSTED")
    private Instant datePosted;

    /** The content of the CommentPost. */
    @MappedProperty("CONTENT")
    private String content;

    /**
     * Constructor for creating a CommentPost. The ID and dateCreated is
     * automatically generated.
     * 
     * @param user    the User of the CommentPost to set
     * @param article the Article of the CommentPost to set
     * @param score   the net score of the CommentPost to set
     * @param content the content of the CommentPost to set
     */
    @Creator
    public CommentPost(@Nullable User user, @Nullable Article article, Integer score, String content) {
        this.user = user;
        this.article = article;
        this.score = score;
        this.content = content;
    }

    /**
     * Constructor for creating a CommentPost meant for foreign key.
     *
     * @param id the ID to set
     */
    public CommentPost(Long id) {
        this.id = id;
    }

    /**
     * Gets unique ID of the CommentPost.
     *
     * @return the CommentPost ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets unique ID of the CommentPost.
     *
     * @param id the CommentPost ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the User that posted the CommentPost.
     * 
     * @return the User that posted the CommentPost
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the Article of the CommentPost.
     *
     * @return the Article of the CommentPost
     */
    public Article getArticle() {
        return article;
    }

    /**
     * Gets the score of the CommentPost.
     *
     * @return the score of the CommentPost
     */
    public Integer getScore() {
        return score;
    }

    /**
     * Gets the creation Timestamp of the CommentPost.
     *
     * @return the creation Timestamp of the CommentPost
     */
    public Instant getDatePosted() {
        return datePosted;
    }

    /**
     * Set the Timestamp that the CommentPost was created.
     *
     * @param datePosted to creation Timestamp of the CommentPost
     */
    public void setDatePosted(Instant datePosted) {
        this.datePosted = datePosted;
    }

    /**
     * Gets the content of the CommentPost.
     *
     * @return the content of the CommentPost
     */
    public String getContent() {
        return content;
    }

    /**
     * Increment the score of the CommentPost by 1.
     *
     * @return the score of the CommentPost after upvoting
     */
    public Integer upvote() {
        return ++score;
    }

    /**
     * Decrement the score of the CommentPost by 1.
     *
     * @return the score of the CommentPost after downvoting
     */
    public Integer downvote() {
        return --score;
    }

}
