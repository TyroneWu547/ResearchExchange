package research_exchange.services;

import io.micronaut.data.exceptions.DataAccessException;
import jakarta.inject.Singleton;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.NotFoundException;
import research_exchange.dto.AbstractCommentDTO;
import research_exchange.dto.CommenterDTO;
import research_exchange.dto.FollowupCommentDTO;
import research_exchange.dto.TopLevelCommentDTO;
import research_exchange.dto.UserCommentDTO;
import research_exchange.forms.CommentForm;
import research_exchange.models.Article;
import research_exchange.models.Author;
import research_exchange.models.CommentPost;
import research_exchange.models.GeneralComment;
import research_exchange.models.User;
import research_exchange.repositories.ArticleRepository;
import research_exchange.repositories.AuthorRepository;
import research_exchange.repositories.CommentPostRepository;
import research_exchange.repositories.ExpertRepository;
import research_exchange.repositories.GeneralCommentRepository;
import research_exchange.repositories.UserRepository;

/**
 * The Comment Service class which handles the error checking before inserting
 * into database.
 *
 * @author Tyrone Wu
 */
@Singleton
public class CommentService {

    /** For accessing Article DB table. */
    private final ArticleRepository articleRepository;

    /** For accessing User DB table. */
    private final UserRepository userRepository;

    /** For accessing Author DB table. */
    private final AuthorRepository authorRepository;

    /** For accessing Expert DB table. */
    private final ExpertRepository expertRepository;

    /** For accessing CommentPost DB table. */
    private final CommentPostRepository commentPostRepository;

    /** For accessing GeneralComment DB table. */
    private final GeneralCommentRepository generalCommentRepository;

    /**
     * Setup Article, User, CommentPost, and GeneralComment repository into the
     * Comment service.
     * 
     * @param articleRepository        the Article Repository
     * @param userRepository           the User Repository
     * @param commentPostRepository    the CommentPost Repository
     * @param generalCommentRepository the GeneralComment Repository
     */
    public CommentService(ArticleRepository articleRepository, UserRepository userRepository,
            AuthorRepository authorRepository, ExpertRepository expertRepository,
            CommentPostRepository commentPostRepository,
            GeneralCommentRepository generalCommentRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.expertRepository = expertRepository;
        this.commentPostRepository = commentPostRepository;
        this.generalCommentRepository = generalCommentRepository;
    }

    private void modifyCommentAuthor(boolean approved, List<Author> articleAuthors, CommenterDTO author) {
        if (articleAuthors.stream().anyMatch(a -> a.getUsername().equals(author.getUsername()))) {
            if (!approved) {
                author.setUsername("Anonymous");
                author.setName("Anonymous");
            }
            author.setRole("Author");
        } else if (author.getRole().equals("Expert")) {
            String expertField = expertRepository.findByUsername(author.getUsername()).get().getField();
            author.setRole("Expert in " + expertField);
        }
    }

    private void modifyTopLevelCommentDTO(boolean approved, List<Author> articleAuthors, TopLevelCommentDTO comment) {
        modifyCommentAuthor(approved, articleAuthors, comment.getAuthor());

        for (FollowupCommentDTO followupView : comment.getFollowups()) {
            modifyCommentAuthor(approved, articleAuthors, followupView.getAuthor());
        }
    }

    /**
     * Get all the Comments associated with the Article ID. A NotFoundException is
     * thrown if the Article post does not exist.
     * 
     * @param articleId the Article ID used to retrieve the Comments
     * @return array of Comments associated with the Article ID
     * @throws NotFoundException if the Article does not exist in the database
     */
    public List<TopLevelCommentDTO> getArticleAllComments(Long articleId) {
        // Check if Article exists
        if (!articleRepository.existsById(articleId)) {
            throw new NotFoundException("Article does not exist.");
        }

        List<GeneralComment> allComments = generalCommentRepository.findAll();
        List<TopLevelCommentDTO> topLevelCommentsView = new LinkedList<>();
        List<Author> articleAuthors = authorRepository.findAllByArticleId(articleId);
        boolean approved = articleRepository.findById(articleId).get().getApproved() == 1;
        for (GeneralComment gc : allComments) {
            if (gc.getCommentPost().getArticle().getId().equals(articleId) && gc.getReplyToId() == null) {
                List<GeneralComment> followupComments = generalCommentRepository
                        .findAllByRootThreadId(gc.getCommentPost().getId());
                TopLevelCommentDTO view = new TopLevelCommentDTO(gc.getCommentPost(),
                        new CommenterDTO(gc.getCommentPost().getUser()), followupComments);
                modifyTopLevelCommentDTO(approved, articleAuthors, view);
                topLevelCommentsView.add(view);
            }
        }
        return topLevelCommentsView;
    }

    /**
     * Get all the Comments associated with the User ID. A NotFoundException is
     * thrown if the User does not exist.
     * 
     * @param username the username used to retrieve the Comments
     * @return array of Comments associated with the User ID
     * @throws NotFoundException if the User does not exist in the database
     */
    public List<UserCommentDTO> getUserAllComments(String username) throws NotFoundException {
        // Check if User exists
        Optional<Long> userId = userRepository.findIdByUsername(username);
        if (userId.isEmpty()) {
            throw new NotFoundException("User does not exist.");
        }

        List<GeneralComment> allComments = generalCommentRepository.findAll();
        List<UserCommentDTO> userCommentsView = new LinkedList<>();
        for (GeneralComment gc : allComments) {
            if (gc.getCommentPost().getUser().getId().equals(userId.get())) {
                userCommentsView.add(new UserCommentDTO(gc.getCommentPost()));
            }
        }

        return userCommentsView;
    }

    /**
     * Post Comment to an Article post. A NotFoundException is thrown if the User,
     * Article, or CommentPost does not exist.
     * An IllegalArgumentException is thrown if the Comment is empty.
     *
     * @param articleId   the Article ID of where to post the Comment
     * @param commentForm the Comment to post
     * @return the posted Comment
     * @throws NotFoundException        if the User, Article, or Comment does not
     *                                  exist in the database
     * @throws IllegalArgumentException if Comment is empty
     */
    public AbstractCommentDTO postComment(Long articleId, CommentForm commentForm) {
        // Check if User exists
        Optional<Long> userId = userRepository.findIdByUsername(commentForm.getAuthor());
        if (userId.isEmpty()) {
            throw new NotFoundException("User does not exist.");
        }
        // Check if Article exists
        else if (!articleRepository.existsById(articleId)) {
            throw new NotFoundException("Article does not exist.");
        }
        // Check if CommentPost reference exists if they're non-null
        else if (commentForm.getRootThreadId() != null && commentForm.getReplyToId() != null) {
            if (!commentPostRepository.existsById(commentForm.getRootThreadId()) ||
                    !commentPostRepository.existsById(commentForm.getReplyToId())) {
                throw new NotFoundException("Referenced Comment does not exist.");
            }
        }
        // Check if Comment is empty
        if (commentForm.getContent() == null || commentForm.getContent().isEmpty()) {
            throw new IllegalArgumentException("Comment is empty.");
        }

        try {
            // Save comment
            CommentPost commentPost = new CommentPost(new User(userId.get()), new Article(articleId), 0,
                    commentForm.getContent());
            GeneralComment generalComment = new GeneralComment(commentPost,
                    new CommentPost(commentForm.getRootThreadId()),
                    new CommentPost(commentForm.getReplyToId()));
            generalComment = generalCommentRepository.save(generalComment);

            // Return the saved comment in the view format
            if (commentForm.getRootThreadId() == null) {
                // If it is a top-level comment
                return new TopLevelCommentDTO(generalComment.getCommentPost(),
                        new CommenterDTO(commentForm.getAuthor()), null);
            } else {
                // If it is a followup comment
                return new FollowupCommentDTO(generalComment, new CommenterDTO(commentForm.getAuthor()));
            }
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Upvote or downvote a Comment post from the User. A NotFoundException is
     * thrown if Comment does not exist. If the vote action is
     * not valid, then an IllegalArgumentException is thrown.
     *
     * @param commentPostId the Comment ID to upvote/downvote
     * @param which         the vote action, 'up' or 'down'
     * @return the updated score of the Comment
     * @throws NotFoundException        if the Comment does not exist in the
     *                                  database
     * @throws IllegalArgumentException if the vote action is not 'up' or 'down'
     */
    public Integer voteComment(Long commentPostId, String which) {
        // Check if CommentPost exists
        Optional<CommentPost> commentPost = commentPostRepository.findById(commentPostId);
        if (commentPost.isEmpty()) {
            throw new NotFoundException("Comment post does not exist.");
        }
        // Check if vote action is valid
        else if (!which.equals("up") && !which.equals("down")) {
            throw new IllegalArgumentException("Vote must be 'up' or 'down'.");
        }
        // Update score
        Integer score = which.equals("up") ? commentPost.get().upvote() : commentPost.get().downvote();
        commentPostRepository.update(commentPostId, score);
        return score;
    }

}
