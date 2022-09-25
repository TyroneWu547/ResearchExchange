package research_exchange.forms;

import java.util.List;

/**
 * The InlineComment Form class which takes in an Inline Comment.
 *
 * @author Tyrone Wu
 */
public class InlineCommentForm {

    /** The page number that the InlineComment is on. */
    private Integer pageNum;

    /** The content that the InlineComment is selecting. */
    private String selectedContent;

    /** The content of the InlineComment. */
    private String content;

    /** The list of highlight sections that the InlineComment references. */
    private List<HighlightSectionForm> highlightSections;

    /**
     * The constructor for receiving a submitted inline comment.
     *
     * @param pageNum           the page number of the inline comment
     * @param selectedContent   the select content of the inline comment
     * @param content           the contents of the inline comment
     * @param highlightSections the highlight sections of the inline comment
     */
    public InlineCommentForm(Integer pageNum, String selectedContent, String content,
            List<HighlightSectionForm> highlightSections) {
        this.pageNum = pageNum;
        this.selectedContent = selectedContent;
        this.content = content;
        this.highlightSections = highlightSections;
    }

    /**
     * Default constructor for InlineComment Form.
     */
    public InlineCommentForm() {
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
     * Get the contents of the InlineComment.
     *
     * @return the content of the InlineComment
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the highlight sections of the InlineComment.
     *
     * @return the highlight sections of the InlineComment
     */
    public List<HighlightSectionForm> getHighlightSections() {
        return highlightSections;
    }

}
