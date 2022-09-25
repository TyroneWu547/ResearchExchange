package research_exchange.dto;

import research_exchange.models.CommentPost;

/**
 * The view for a Comment on User profile.
 *
 * @author Tyrone Wu
 */
public class UserCommentDTO extends AbstractCommentDTO {

    /** The article ID that the Comment is on. */
    private Long articleId;

    /** The article Title that the Comment is on. */
    private String articleTitle;

    /**
     * Constructor for creating a generic Comment view.
     *
     * @param comment the CommentPost which contains the ID, score, date posted,
     *                content, and article info
     */
    public UserCommentDTO(CommentPost comment) {
        super(comment, new CommenterDTO(comment.getUser()));
        this.articleId = comment.getArticle().getId();
        this.articleTitle = comment.getArticle().getName();
    }

    /**
     * Default constructor for UserCommentView.
     */
    public UserCommentDTO() {
    }

    /**
     * Get the Article ID of the Comment.
     *
     * @return the Comment's Article ID
     */
    public Long getArticleId() {
        return articleId;
    }

    /**
     * Get the Article title of the Comment.
     *
     * @return the Comment's Article title
     */
    public String getArticleTitle() {
        return articleTitle;
    }

}
