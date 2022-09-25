package research_exchange.dto;

import java.util.List;

import research_exchange.models.ExpertReview;
import research_exchange.models.GeneralComment;

/**
 * The view for an Expert Review on user profile.
 *
 * @author Tyrone Wu
 */
public class UserReviewDTO extends ExpertReviewDTO {

    /** The article ID that the ExpertReview is on. */
    private Long articleId;

    /** The article title that the ExpertReview is on. */
    private String articleTitle;

    /**
     * The constructor for creating an ExpertReview user's view.
     *
     * @param expertReview     the ExpertReview model to format the view
     * @param author           the author of the ExpertReview
     * @param generalFollowups the array of followups to the ExpertReview
     * @param inlineComments   the inline comments of the ExpertReview
     */
    public UserReviewDTO(ExpertReview expertReview, CommenterDTO author, List<GeneralComment> generalFollowups,
            List<InlineCommentDTO> inlineComments) {
        super(expertReview, author, generalFollowups, inlineComments);
        this.articleId = expertReview.getCommentPost().getArticle().getId();
        this.articleTitle = expertReview.getCommentPost().getArticle().getName();
    }

    /**
     * Default constructor for an ExpertReview user's view.
     */
    public UserReviewDTO() {
    }

    /**
     * Gets the article ID that the ExpertReview is on.
     *
     * @return the article ID that the ExpertReview is on
     */
    public Long getArticleId() {
        return articleId;
    }

    /**
     * Gets the article title that the ExpertReview is on.
     *
     * @return the article title that the ExpertReview is on
     */
    public String getArticleTitle() {
        return articleTitle;
    }

}
