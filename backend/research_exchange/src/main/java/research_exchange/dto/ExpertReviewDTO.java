package research_exchange.dto;

import java.util.List;

import research_exchange.models.ExpertReview;
import research_exchange.models.GeneralComment;

/**
 * The view for an Expert Review.
 *
 * @author Tyrone Wu
 */
public class ExpertReviewDTO extends TopLevelCommentDTO {

    /** The status of the ExpertReview. */
    private String status;

    /** The inline comments of the ExpertReview. */
    private List<InlineCommentDTO> inlineComments;

    /**
     * The constructor for creating an ExpertReview View.
     *
     * @param expertReview     the ExpertReview model to format the view
     * @param author           the author of the ExpertReview
     * @param generalFollowups the array of followups to the ExpertReview
     * @param inlineComments   the inline comments of the ExpertReview
     */
    public ExpertReviewDTO(ExpertReview expertReview, CommenterDTO author, List<GeneralComment> generalFollowups,
            List<InlineCommentDTO> inlineComments) {
        super(expertReview.getCommentPost(), author, generalFollowups);
        this.status = expertReview.getStatus();
        this.inlineComments = inlineComments;
    }

    /**
     * Default constructor for ExpertReview View.
     */
    public ExpertReviewDTO() {
    }

    /**
     * Gets the status of the ExpertReview.
     *
     * @return the status of the ExpertReview
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the inline comments of the ExpertReview.
     *
     * @return the inline comments of the ExpertReview
     */
    public List<InlineCommentDTO> getInlineComments() {
        return inlineComments;
    }

}
