package research_exchange.dto;

import java.util.LinkedList;
import java.util.List;

import research_exchange.models.GeneralComment;
import research_exchange.models.HighlightSection;
import research_exchange.models.InlineComment;

/**
 * The view for an Inline Comment.
 *
 * @author Tyrone Wu
 */
public class InlineCommentDTO extends TopLevelCommentDTO {

    /** The page number of the InlineComment. */
    private Integer pageNum;

    /** The selected content of the InlineComment. */
    private String selectedContent;

    /** The highlight sections of the InlineComment. */
    private List<HighlightSectionDTO> highlightSections;

    /**
     * The constructor for creating an InlineComment View.
     *
     * @param inlineComment    the InlineComment model to format the view
     * @param author           the author of the InlineComment
     * @param generalFollowups the array of followups to the InlineComment
     */
    public InlineCommentDTO(InlineComment inlineComment, CommenterDTO author, List<GeneralComment> generalFollowups) {
        super(inlineComment.getCommentPost(), author, generalFollowups);
        this.pageNum = inlineComment.getPageNum();
        this.selectedContent = inlineComment.getSelectedContent();

        this.highlightSections = new LinkedList<>();
        for (HighlightSection hs : inlineComment.getHighlightSections()) {
            this.highlightSections.add(new HighlightSectionDTO(hs));
        }
    }

    /**
     * Default constructor for InlineComment View.
     */
    public InlineCommentDTO() {
    }

    /**
     * Get the page number of the InlineComment.
     *
     * @return the page number of the InlineComment
     */
    public Integer getPageNum() {
        return pageNum;
    }

    /**
     * Get the selected content of the InlineComment.
     *
     * @return the selected content of the InlineComment
     */
    public String getSelectedContent() {
        return selectedContent;
    }

    /**
     * Get the highlight sections of the InlineComment.
     *
     * @return the highlight sections of the InlineComment
     */
    public List<HighlightSectionDTO> getHighlightSections() {
        return highlightSections;
    }

}
