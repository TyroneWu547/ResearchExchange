package research_exchange.forms;

import java.util.List;

/**
 * The ExpertReview Form class which takes in an Expert Review.
 *
 * @author Tyrone Wu
 */
public class ExpertReviewForm {

    /** The author of the ExpertReview. */
    private String author;

    /** The content of the ExpertReview. */
    private String content;

    /** The status of the ExpertReview. */
    private String status;

    /** The inline comments of the ExpertReview */
    private List<InlineCommentForm> inlineComments;

    /**
     * The constructor for receiving a submitted ExpertReview.
     *
     * @param author         the author of the ExpertReview
     * @param content        the contents of the ExpertReview
     * @param status         the status of the ExpertReview
     * @param inlineComments the inline comments of the ExpertReview
     */
    public ExpertReviewForm(String author, String content, String status, List<InlineCommentForm> inlineComments) {
        this.author = author;
        this.content = content;
        this.status = status;
        this.inlineComments = inlineComments;
    }

    /**
     * Default constructor for ExpertReview Form.
     */
    public ExpertReviewForm() {
    }

    /**
     * Get the author of the ExpertReview.
     *
     * @return the author of the ExpertReview.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the contents of the ExpertReview.
     *
     * @return the content of the ExpertReview
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the status of the ExpertReview.
     *
     * @return the status of the ExpertReview
     */
    public String getStatus() {
        return status;
    }

    /**
     * Get the inline comments of the ExpertReview.
     *
     * @return the inline comments of the ExpertReview
     */
    public List<InlineCommentForm> getInlineComments() {
        return inlineComments;
    }

}
