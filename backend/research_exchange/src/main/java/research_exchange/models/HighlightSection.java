package research_exchange.models;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;
import research_exchange.forms.HighlightSectionForm;

/**
 * The Inline Comment entity class which has a foreign key reference to Expert
 * Review.
 * 
 * @author Tyrone Wu
 */
@Introspected
@MappedEntity("HIGHLIGHT_SECTION")
public class HighlightSection {

    /** Unique ID for Highlight Section entity. */
    @Id
    @GeneratedValue
    @MappedProperty("ID")
    private Long id;

    /** Foreign key to Inline Comment. */
    @Relation(Kind.MANY_TO_ONE)
    @MappedProperty("INLINE_COMMENT_ID")
    private InlineComment inlineComment;

    /** The x position of the Highlight Section. */
    @MappedProperty("X_POSITION")
    private Integer xPosition;

    /** The y position of the Highlight Section. */
    @MappedProperty("Y_POSITION")
    private Integer yPosition;

    /** The width of the Highlight Section. */
    @MappedProperty("WIDTH")
    private Integer width;

    /** The height of the Highlight Section. */
    @MappedProperty("HEIGHT")
    private Integer height;

    /**
     * Constructor for creating a Highlight Section associated with an Inline
     * Comment The foreign key to InlineComment
     * will automatically be set.
     * 
     * @param inlineComment the inline comment that the Highlight Section is apart
     *                      of to set
     * @param xPosition     the x position of the Highlight Section to set
     * @param yPosition     the y position of the Highlight Section to set
     * @param width         the width of the Highlight Section to set
     * @param height        the height of the Highlight Section to set
     */
    @Creator
    public HighlightSection(@Nullable InlineComment inlineComment, Integer xPosition, Integer yPosition, Integer width,
            Integer height) {
        this.inlineComment = inlineComment;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructor for creating Highlight Section with the form object.
     *
     * @param inlineComment        the inline comment that the Highlight Section is
     *                             apart of to set
     * @param highlightSectionForm the form to set the xPosition, yPosition, width,
     *                             and height
     */
    public HighlightSection(InlineComment inlineComment, HighlightSectionForm highlightSectionForm) {
        this.inlineComment = inlineComment;
        this.xPosition = highlightSectionForm.getX();
        this.yPosition = highlightSectionForm.getY();
        this.width = highlightSectionForm.getWidth();
        this.height = highlightSectionForm.getHeight();
    }

    /**
     * Gets unique ID of the Highlight Section.
     *
     * @return the ID of the Highlight Section as Long
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique ID of the Highlight Section.
     *
     * @param id the unique ID of Highlight Section to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the Inline Comment of the Highlight Section.
     *
     * @return the Inline Comment object of the Highlight Section
     */
    public InlineComment getInlineComment() {
        return inlineComment;
    }

    /**
     * Gets the x position of the Highlight Section.
     *
     * @return the x position of the Highlight Section
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * Gets the y position of the Highlight Section.
     *
     * @return the y position of the Highlight Section
     */
    public int getYPosition() {
        return yPosition;
    }

    /**
     * Gets the width of the Highlight Section.
     *
     * @return the width of the Highlight Section
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the Highlight Section.
     *
     * @return the height of the Highlight Section
     */
    public int getHeight() {
        return height;
    }

}
