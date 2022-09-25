package research_exchange.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.NotFoundException;
import research_exchange.services.CommentService;
import research_exchange.forms.CommentForm;
import research_exchange.dto.AbstractCommentDTO;
import research_exchange.dto.TopLevelCommentDTO;
import research_exchange.dto.FollowupCommentDTO;
import research_exchange.dto.UserCommentDTO;

/**
 * The Comment Controller class which handles the API endpoints related to
 * Comment and FollowupComment.
 *
 * @author Tyrone Wu
 */
@Controller("/")
@Secured(SecurityRule.IS_ANONYMOUS)
@ExecuteOn(TaskExecutors.IO)
public class CommentController {

    /** The Comment Service class. */
    private final CommentService commentService;

    /**
     * Sets the Comment Service class for error handling.
     *
     * @param commentService the CommentService to set
     */
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * The API endpoint for getting all Comments on an Article post.
     *
     * @param articleId the Article post ID
     * @return response OK with list of Comments; otherwise, response NOT_FOUND if
     *         Article post does not exist
     */
    @Get(value = "/articles/{articleId}/comments")
    public HttpResponse<?> getArticleAllComments(@NotBlank Long articleId) {
        try {
            List<TopLevelCommentDTO> articleComments = commentService.getArticleAllComments(articleId);
            return HttpResponse.status(HttpStatus.OK).body(articleComments);
        } catch (NotFoundException e) {
            return HttpResponse.status(HttpStatus.NOT_FOUND)
                    .body("Status " + HttpStatus.NOT_FOUND + ": " + e.getMessage());
        }
    }

    /**
     * The API endpoint for getting all Comments from a User.
     *
     * @param username the username of the User
     * @return response OK with list of Comments from the User; otherwise, response
     *         NOT_FOUND if User does not exist
     */
    @Get(value = "/users/{username}/comments")
    public HttpResponse<?> getUserAllComments(@NotBlank String username) {
        try {
            List<UserCommentDTO> userComments = commentService.getUserAllComments(username);
            return HttpResponse.status(HttpStatus.OK).body(userComments);
        } catch (NotFoundException e) {
            return HttpResponse.status(HttpStatus.NOT_FOUND)
                    .body("Status " + HttpStatus.NOT_FOUND + ": " + e.getMessage());
        }
    }

    /**
     * The API endpoint for posting a Comment on an Article post.
     *
     * @param articleId the Article post ID
     * @param comment   the Comment to post
     * @return response OK with list of Comments; NOT_FOUND if Article post does not
     *         exist; UNPROCESSABLE_ENTITY if Comment is invalid/empty
     */
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Post(value = "/articles/{articleId}/post-comment", consumes = { "application/json" })
    public HttpResponse<?> postComment(Authentication authentication, @NotBlank Long articleId,
            @Body CommentForm commentForm) {
        if (!authentication.getName().equals(commentForm.getAuthor())) {
            return HttpResponse.unauthorized();
        }

        try {
            AbstractCommentDTO comment = commentService.postComment(articleId, commentForm);
            return HttpResponse.status(HttpStatus.CREATED)
                    .body((comment instanceof TopLevelCommentDTO) ? (TopLevelCommentDTO) comment
                            : (FollowupCommentDTO) comment);
        } catch (NotFoundException e) {
            return HttpResponse.status(HttpStatus.NOT_FOUND)
                    .body("Status " + HttpStatus.NOT_FOUND + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return HttpResponse.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Status " + HttpStatus.UNPROCESSABLE_ENTITY + ": " + e.getMessage());
        }
    }

    /**
     * The API endpoint for upvoting or downvoting a Comment.
     *
     * @param commentId the Comment ID to upvote/downvote
     * @return response OK with the Comment score after the upvote/downvote;
     *         response NOT_FOUND if Article or Comment does not exist;
     *         BAD_REQUEST if vote action is invalid
     */
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Post(value = "/comment-posts/{commentPostId}/vote", consumes = { "application/json" })
    public HttpResponse<?> voteComment(@NotBlank Long commentPostId, @Body String which) {
        try {
            Integer commentScore = commentService.voteComment(commentPostId, which);
            return HttpResponse.status(HttpStatus.OK).body(commentScore);
        } catch (NotFoundException e) {
            return HttpResponse.status(HttpStatus.NOT_FOUND)
                    .body("Status " + HttpStatus.NOT_FOUND + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST)
                    .body("Status " + HttpStatus.BAD_REQUEST + ": " + e.getMessage());
        }
    }

}
