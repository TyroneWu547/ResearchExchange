package research_exchange.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

import research_exchange.models.CommentPost;

/**
 * The abstract class for the view of a generic Comment.
 *
 * @author Tyrone Wu
 */
public abstract class AbstractCommentDTO {

    /** Unique ID for Comment. */
    private Long id;

    /** The author of the Comment. */
    private CommenterDTO author;

    /** The Comment's net score. */
    private Integer score;

    /** Date the Comment was posted. */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "EST")
    private Instant datePosted;

    /** The content of the Comment. */
    private String content;

    /**
     * Constructor for creating a generic Comment view.
     *
     * @param comment the CommentPost which contains the ID, score, date posted, and
     *                content
     * @param author  the author of the Comment
     */
    public AbstractCommentDTO(CommentPost comment, CommenterDTO author) {
        this.id = comment.getId();
        this.author = author;
        this.score = comment.getScore();
        this.datePosted = comment.getDatePosted();
        this.content = comment.getContent();
    }

    /**
     * Default constructor for AbstractCommentView.
     */
    public AbstractCommentDTO() {
    }

    /**
     * Get the ID of the Comment.
     *
     * @return the Comment ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the author of the Comment.
     *
     * @return the Comment author
     */
    public CommenterDTO getAuthor() {
        return author;
    }

    /**
     * Get the score of the Comment.
     *
     * @return the Comment score
     */
    public Integer getScore() {
        return score;
    }

    /**
     * Get the creation date of the Comment.
     *
     * @return the Comment creation date
     */
    public Instant getDatePosted() {
        return datePosted;
    }

    /**
     * Get the content of the Comment.
     *
     * @return the Comment's content
     */
    public String getContent() {
        return content;
    }

}
