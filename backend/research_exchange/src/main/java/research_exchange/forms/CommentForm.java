package research_exchange.forms;

/**
 * The Comment Form class which takes in a comment or followup.
 *
 * @author Tyrone Wu
 */
public class CommentForm {

    /** The thread that the comment is apart of. (optional) */
    private Long rootThreadId;

    /** The ID that the comment is replying to. (optional) */
    private Long replyToId;

    /** The author of the comment. */
    private String author;

    /** The content of the comment. */
    private String content;

    /**
     * The constructor for receiving a submitted comment.
     *
     * @param rootThreadId the root thread to set
     * @param replyToId    the comment replying to to set
     * @param author       the author to set
     * @param content      the content to set
     */
    public CommentForm(Long rootThreadId, Long replyToId, String author, String content) {
        this.rootThreadId = rootThreadId;
        this.replyToId = replyToId;
        this.author = author;
        this.content = content;
    }

    /**
     * Default constructor for comment form.
     */
    public CommentForm() {
    }

    /**
     * Get the root thread UUID of the comment.
     *
     * @return the root thread UUID
     */
    public Long getRootThreadId() {
        return rootThreadId;
    }

    /**
     * Get the reply to UUID of the comment.
     *
     * @return the reply to UUID
     */
    public Long getReplyToId() {
        return replyToId;
    }

    /**
     * Get the author of the comment.
     *
     * @return the author of the comment.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the contents of the comment.
     *
     * @return the content of the comment
     */
    public String getContent() {
        return content;
    }

}
