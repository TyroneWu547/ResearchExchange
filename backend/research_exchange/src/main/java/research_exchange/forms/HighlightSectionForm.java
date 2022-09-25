package research_exchange.forms;

/**
 * The HighlightSection Form class which takes in a Highlight Section.
 *
 * @author Tyrone Wu
 */
public class HighlightSectionForm {

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
     * @param xPosition the x position of the HighlightSection
     * @param yPosition the y position of the HighlightSection
     * @param width     the width of the HighlightSection
     * @param height    the height of the HighlightSection
     */
    public HighlightSectionForm(Integer xPosition, Integer yPosition, Integer width, Integer height) {
        this.x = xPosition;
        this.y = yPosition;
        this.width = width;
        this.height = height;
    }

    /**
     * Default constructor for HighlightSectionForm.
     */
    public HighlightSectionForm() {
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
