package research_exchange.models;

import java.util.List;
import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;

/**
 * The Inline Comment entity class which has a foreign key reference to Expert
 * Review.
 * 
 * @author Tyrone Wu
 */
@MappedEntity("INLINE_COMMENT")
public class InlineComment {

    /** Unique ID for Inline Comment entity. */
    @Id
    @GeneratedValue
    @MappedProperty("ID")
    private Long id;

    /** Foreign key to CommentPost. */
    @Relation(value = Kind.ONE_TO_ONE, cascade = Relation.Cascade.ALL)
    @MappedProperty("COMMENT_POST_ID")
    private CommentPost commentPost;

    /** Foreign key to Expert Review. */
    @Relation(Kind.MANY_TO_ONE)
    @MappedProperty("EXPERT_REVIEW_ID")
    private ExpertReview expertReview;

    /** The page number of the Inline Comment. */
    @MappedProperty("PAGE_NUM")
    private Integer pageNum;

    /** The content that the Inline Comment selected. */
    @MappedProperty("SELECTED_CONTENT")
    private String selectedContent;

    /**
     * List of Highlight Sections foreign references associated with the Inline
     * Comment.
     */
    @Relation(value = Kind.ONE_TO_MANY, mappedBy = "inlineComment", cascade = Relation.Cascade.ALL)
    private List<HighlightSection> highlightSections;

    /**
     * Constructor for creating an Inline Comment associated with an Expert Review.
     * The foreign key to ExpertReview will automatically be set.
     * 
     * @param commentPost     the abstract CommentPost to set
     * @param expertReview    the Inline Comment is apart of to set
     * @param pageNum         the page number of the Inline Comment to set
     * @param selectedContent the selected text of the Inline Comment to set
     */
    @Creator
    public InlineComment(@Nullable CommentPost commentPost, @Nullable ExpertReview expertReview, Integer pageNum,
            String selectedContent) {
        this.commentPost = commentPost;
        this.expertReview = expertReview;
        this.pageNum = pageNum;
        this.selectedContent = selectedContent;
    }

    /**
     * Gets unique ID of the Inline Comment.
     *
     * @return the ID of the Inline Comment as Long
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique ID of the Inline Comment.
     *
     * @param id the unique ID of Inline Comment to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the abstract CommentPost of the Inline Comment.
     *
     * @return the abstract CommentPost of the Inline Comment
     */
    public CommentPost getCommentPost() {
        return commentPost;
    }

    /**
     * Gets the Expert Review of the Inline Comment.
     *
     * @return the Expert Review object of the Inline Comment
     */
    public ExpertReview getExpertReview() {
        return expertReview;
    }

    /**
     * Gets the page number of the Inline Comment.
     *
     * @return the page number of the Inline Comment
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * Gets the selected text of the Inline Comment.
     *
     * @return the selected text of the Inline Comment
     */
    public String getSelectedContent() {
        return selectedContent;
    }

    /**
     * Gets the Highlight Sections associated with the Inline Comment.
     *
     * @return the Highlight Sections associated with the Inline Comment
     */
    public List<HighlightSection> getHighlightSections() {
        return highlightSections;
    }

    /**
     * Sets the Highlight Sections associated with the Inline Comment.
     *
     * @return the Highlight Sections associated with the Inline Comment to set
     */
    public void setHighlightSections(List<HighlightSection> highlightSections) {
        this.highlightSections = highlightSections;
    }

}
