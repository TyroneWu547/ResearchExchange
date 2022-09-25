package research_exchange.dto;

import research_exchange.models.GeneralComment;

/**
 * The view for a top-level Comment.
 *
 * @author Tyrone Wu
 */
public class FollowupCommentDTO extends AbstractCommentDTO {

    /** The ID that the followup Comment is replying to. */
    private Long replyingTo;

    /**
     * Constructor for creating a followup Comment view.
     *
     * @param comment the GeneralComment model to format the view
     * @param author  the author of the Comment
     */
    public FollowupCommentDTO(GeneralComment comment, CommenterDTO author) {
        super(comment.getCommentPost(), author);
        this.replyingTo = comment.getReplyToId();
    }

    /**
     * Default constructor for FollowupCommentView.
     */
    public FollowupCommentDTO() {
    }

    /**
     * Get the reply-to ID of the followup Comment.
     *
     * @return the followup Comment's reply-to ID
     */
    public Long getReplyingTo() {
        return replyingTo;
    }

}
