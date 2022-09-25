package research_exchange.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import research_exchange.forms.ExpertReviewForm;
import research_exchange.services.ExpertReviewService;
import research_exchange.dto.ExpertReviewDTO;
import research_exchange.dto.UserReviewDTO;

/**
 * The Expert Review Controller class which handles the API endpoints related to
 * Expert Reviews.
 *
 * @author Tyrone Wu
 */
@Controller("/")
@Secured(SecurityRule.IS_ANONYMOUS)
@ExecuteOn(TaskExecutors.IO)
public class ExpertReviewController {

    /** The Expert Review Service class. */
    private final ExpertReviewService expertReviewService;

    /**
     * Sets the Expert Review Service class for error handling.
     *
     * @param expertReviewService the Expert Review Service to set
     */
    public ExpertReviewController(ExpertReviewService expertReviewService) {
        this.expertReviewService = expertReviewService;
    }

    /**
     * The API endpoint for getting all Expert Reviews on an Article post.
     *
     * @param articleId the Article post ID
     * @return response OK with list of Expert Reviews; otherwise, response
     *         NOT_FOUND if Article post does not exist
     */
    @Get(value = "/articles/{articleId}/expert-reviews")
    public HttpResponse<?> getArticleExpertReviewInfo(@NotBlank Long articleId) {
        try {
            List<ExpertReviewDTO> articleExpertReviews = expertReviewService.getArticleExpertReviewInfo(articleId);
            return HttpResponse.status(HttpStatus.OK).body(articleExpertReviews);
        } catch (NotFoundException e) {
            return HttpResponse.status(HttpStatus.NOT_FOUND)
                    .body("Status " + HttpStatus.NOT_FOUND + ": " + e.getMessage());
        }
    }

    /**
     * The API endpoint for getting all Expert Reviews from a User.
     *
     * @param username the username of the User
     * @return response OK with list of Expert Reviews from the User; otherwise,
     *         response NOT_FOUND if User does not exist
     */
    @Get(value = "/users/{username}/expert-reviews")
    public HttpResponse<?> getUserAllExpertReviews(@NotBlank String username) {
        try {
            List<UserReviewDTO> userExpertReviews = expertReviewService.getUserAllExpertReviews(username);
            return HttpResponse.status(HttpStatus.OK).body(userExpertReviews);
        } catch (NotFoundException e) {
            return HttpResponse.status(HttpStatus.NOT_FOUND)
                    .body("Status " + HttpStatus.NOT_FOUND + ": " + e.getMessage());
        }
    }

    /**
     * The API endpoint for getting an Expert Review on an Article post.
     *
     * @param expertReviewId the Expert Review ID
     * @return response OK with the Expert Review; otherwise, response NOT_FOUND if
     *         Article or Expert Review does not exist
     */
    @Get(value = "/expert-reviews/{expertReviewId}")
    public HttpResponse<?> getExpertReviewInfo(@NotBlank Long expertReviewId) {
        try {
            ExpertReviewDTO expertReview = expertReviewService.getExpertReviewInfo(expertReviewId);
            return HttpResponse.status(HttpStatus.OK).body(expertReview);
        } catch (NotFoundException e) {
            return HttpResponse.status(HttpStatus.NOT_FOUND)
                    .body("Status " + HttpStatus.NOT_FOUND + ": " + e.getMessage());
        }
    }

    /**
     * PUT request for editing status of the expert review.
     *
     * @param expertReviewId the expert review to edit
     * @param newStatus      the new status to update with
     */
    @Secured({ "Expert" })
    @Put(value = "/expert-reviews/{expertReviewId}/change-status")
    public HttpResponse<?> editExpertReviewStatus(Authentication authentication, @NotBlank Long expertReviewId,
            @Body String newStatus) {
        try {
            ExpertReviewDTO expertReview = expertReviewService.editExpertReviewStatus(authentication.getName(),
                    expertReviewId, newStatus);
            return HttpResponse.status(HttpStatus.OK).body(expertReview);
        } catch (NotFoundException e) {
            return HttpResponse.status(HttpStatus.NOT_FOUND)
                    .body("Status " + HttpStatus.NOT_FOUND + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST)
                    .body("Status " + HttpStatus.BAD_REQUEST + ": " + e.getMessage());
        } catch (NotAuthorizedException e) {
            return HttpResponse.status(HttpStatus.UNAUTHORIZED)
                    .body("Status " + HttpStatus.UNAUTHORIZED + ": " + e.getMessage());
        }
    }

    /**
     * The API endpoint for posting a Expert Review on an Article post.
     *
     * @param articleId        the Article post ID
     * @param expertReviewForm the Expert Review to post
     * @return response OK with list of Expert Reviews; NOT_FOUND if Article post
     *         does not exist; UNPROCESSABLE_ENTITY if Expert Review is
     *         invalid/empty
     */
    @Secured({ "Expert" })
    @Post(value = "/articles/{articleId}/review-article", consumes = { "application/json" })
    public HttpResponse<?> postExpertReview(Authentication authentication, @NotBlank Long articleId,
            @Body ExpertReviewForm expertReviewForm) {
        if (!authentication.getName().equals(expertReviewForm.getAuthor())) {
            return HttpResponse.unauthorized();
        }

        try {
            ExpertReviewDTO expertReview = expertReviewService.postExpertReview(articleId, expertReviewForm);
            return HttpResponse.status(HttpStatus.CREATED).body(expertReview);
        } catch (NotFoundException e) {
            return HttpResponse.status(HttpStatus.NOT_FOUND)
                    .body("Status " + HttpStatus.NOT_FOUND + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return HttpResponse.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Status " + HttpStatus.UNPROCESSABLE_ENTITY + ": " + e.getMessage());
        }
    }

}
