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
 * The GeneralComment entity class that represents a general comment. This has a
 * foreign key to the abstract CommentPost, an optional GeneralComment thread,
 * and another optional reply-to GeneralComment.
 * 
 * @author Tyrone Wu
 */
@MappedEntity("GENERAL_COMMENT")
public class GeneralComment {

    /** Unique ID for GeneralComment entity. */
    @Id
    @GeneratedValue
    @MappedProperty("ID")
    private Long id;

    /** Foreign key to GeneralComment */
    @Relation(value = Kind.ONE_TO_ONE, cascade = Relation.Cascade.ALL)
    @MappedProperty("COMMENT_POST_ID")
    private CommentPost commentPost;

    /** Comment foreign key of root thread that the current Comment is on. */
    @Nullable
    @Relation(Kind.MANY_TO_ONE)
    @MappedProperty("THREAD_ID")
    private CommentPost rootThread;

    /** Comment foreign key object that the current Comment is replying to. */
    @Nullable
    @Relation(Kind.MANY_TO_ONE)
    @MappedProperty("REPLY_TO_ID")
    private CommentPost replyTo;

    /**
     * Comment foreign key ID that the current Comment is replying to. (generated
     * from DB and not inserted)
     */
    @GeneratedValue
    @MappedProperty("REPLY_TO_ID")
    private Long replyToId;

    /**
     * Constructor for creating a GeneralComment. The ID is automatically generated.
     *
     * @param commentPost the abstract CommentPost to set
     * @param rootThread  the GeneralComment thread to set
     * @param replyTo     the reply-to GeneralComment to set
     */
    @Creator
    public GeneralComment(@Nullable CommentPost commentPost, @Nullable CommentPost rootThread,
            @Nullable CommentPost replyTo) {
        this.commentPost = commentPost;
        this.rootThread = rootThread;
        this.replyTo = replyTo;
    }

    /**
     * Gets unique ID of the GeneralComment.
     *
     * @return the ID of the GeneralComment
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets unique ID of the GeneralComment.
     *
     * @param id the GeneralComment ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the abstract CommentPost of the GeneralComment.
     *
     * @return the abstract CommentPost of the GeneralComment
     */
    public CommentPost getCommentPost() {
        return commentPost;
    }

    /**
     * Gets the GeneralComment thread of the GeneralComment.
     *
     * @return the GeneralComment thread of the GeneralComment
     */
    public CommentPost getRootThread() {
        return rootThread;
    }

    /**
     * Gets the reply-to GeneralComment of the GeneralComment.
     *
     * @return the reply-to GeneralComment thread of the GeneralComment
     */
    public CommentPost getReplyTo() {
        return replyTo;
    }

    /**
     * Get the GeneralComment ID that the current GeneralComment is replying to.
     *
     * @return the GeneralComment ID that the current GeneralComment is replying to
     */
    public Long getReplyToId() {
        return replyToId;
    }

    /**
     * Sets the GeneralComment ID that the current GeneralComment is replying to.
     *
     * @param replyToId the GeneralComment ID that the current GeneralComment is
     *                  replying to
     */
    public void setReplyToId(Long replyToId) {
        this.replyToId = replyToId;
    }

}
