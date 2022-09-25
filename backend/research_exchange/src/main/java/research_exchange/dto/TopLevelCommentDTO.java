package research_exchange.dto;

import java.util.LinkedList;
import java.util.List;

import research_exchange.models.CommentPost;
import research_exchange.models.GeneralComment;

/**
 * The view for a top-level Comment.
 *
 * @author Tyrone Wu
 */
public class TopLevelCommentDTO extends AbstractCommentDTO {

    /** An array of followups to the top-level Comment. */
    private List<FollowupCommentDTO> followups;

    /**
     * Constructor for creating a generic Comment view.
     *
     * @param comment          the CommentPost which contains the ID, score, date
     *                         posted, and content
     * @param author           the author of the Comment
     * @param generalFollowups the list of followups to the top-level Comment
     */
    public TopLevelCommentDTO(CommentPost comment, CommenterDTO author, List<GeneralComment> generalFollowups) {
        super(comment, author);

        this.followups = new LinkedList<>();
        if (generalFollowups != null && !generalFollowups.isEmpty()) {
            for (GeneralComment fc : generalFollowups) {
                this.followups.add(new FollowupCommentDTO(fc, new CommenterDTO(fc.getCommentPost().getUser())));
            }
        }
    }

    /**
     * Default constructor for TopLevelCommentView..
     */
    public TopLevelCommentDTO() {
    }

    /**
     * Get the followup Comments of the top-level Comment.
     *
     * @return the top-level Comment's followup Comments
     */
    public List<FollowupCommentDTO> getFollowups() {
        return followups;
    }

}
