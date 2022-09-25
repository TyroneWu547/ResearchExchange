package research_exchange.dto;

import research_exchange.models.HighlightSection;

/**
 * The view for a Highlight Section.
 *
 * @author Tyrone Wu
 */
public class HighlightSectionDTO {

    /** The x position of the HighlightSection. */
    private Integer x;

    /** The y position of the HighlightSection. */
    private Integer y;

    /** The width of the HighlightSection. */
    private Integer width;

    /** The height of the HighlightSection. */
    private Integer height;

    /**
     * The constructor for receiving a HighlightSection.
     *
     * @param highlightSection the HighlightSection model to format the view
     */
    public HighlightSectionDTO(HighlightSection highlightSection) {
        this.x = highlightSection.getXPosition();
        this.y = highlightSection.getYPosition();
        this.width = highlightSection.getWidth();
        this.height = highlightSection.getHeight();
    }

    /**
     * Default constructor for HighlightSectionForm.
     */
    public HighlightSectionDTO() {
    }

    /**
     * Get the x position of the HighlightSection.
     *
     * @return the x position of the HighlightSection
     */
    public Integer getX() {
        return x;
    }

    /**
     * Get the y position of the HighlightSection.
     *
     * @return the y position of the HighlightSection
     */
    public Integer getY() {
        return y;
    }

    /**
     * Get the width of the HighlightSection.
     *
     * @return the width of the HighlightSection
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Get the height of the HighlightSection.
     *
     * @return the height of the HighlightSection
     */
    public Integer getHeight() {
        return height;
    }

}
