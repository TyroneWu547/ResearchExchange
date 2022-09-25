package research_exchange.api;

import io.micronaut.context.env.Environment;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import static io.micronaut.http.HttpStatus.CREATED;
import static io.micronaut.http.HttpStatus.OK;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import research_exchange.forms.ExpertReviewForm;
import research_exchange.forms.HighlightSectionForm;
import research_exchange.forms.InlineCommentForm;
import research_exchange.models.Article;
import research_exchange.models.ExpertReview;
import research_exchange.models.User;
import research_exchange.repositories.ArticleRepository;
import research_exchange.repositories.ExpertReviewRepository;
import research_exchange.repositories.UserRepository;
import research_exchange.services.ExpertReviewService;
import research_exchange.dto.ExpertReviewDTO;
import research_exchange.dto.HighlightSectionDTO;
import research_exchange.dto.InlineCommentDTO;
import research_exchange.dto.UserReviewDTO;

/**
 * API Test class for ExpertReviewController.
 *
 * @author Tyrone Wu
 */
@MicronautTest(environments = Environment.ORACLE_CLOUD, transactional = false, rollback = true)
public class APIExpertReviewTest {

    /** For accessing User DB table. */
    @Inject
    private UserRepository userRepository;

    /** For accessing Article DB table */
    @Inject
    private ArticleRepository articleRepository;

    /** For accessing Expert Review DB table. */
    @Inject
    private ExpertReviewRepository expertReviewRepository;

    /** Expert Review service for saving and getting. */
    @Inject
    private ExpertReviewService expertReviewService;

    /** Injects root endpoint as http client. */
    @Inject
    @Client("/")
    HttpClient client;

    /**
     * Clears database and initializes User and Article.
     */
    @BeforeEach
    public void setup() {
        // Clear database
        expertReviewRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
        byte[] salt = "salt".getBytes();
        // Add User and Article to DB
        User u1 = new User("User 1", "User1", "username@gmail.com", "password", salt, "role");
        User u2 = new User("User 2", "User2", "username@gmail.com", "password", salt, "role");
        User u3 = new User("User 3", "User3", "username@gmail.com", "password", salt, "role");
        userRepository.saveAll(Arrays.asList(u1, u2, u3));

        Article a1 = new Article("article 1", "content1", "mainfield1", "subfield1", "pdf1", "repo1", "data1");
        articleRepository.save(a1);
    }

    /**
     * Test POST endpoint for posting valid Expert Review on Article post.
     */
    @Test
    public void testPostValidExpertReview() {
        Long articleId = articleRepository.findAll().get(0).getId();

        // -- User 1 posting Expert Review with 'Approved' status on an Article 1
        ExpertReviewForm er = new ExpertReviewForm("User1", "content 1", "Approved", null);

        HttpRequest<?> req = HttpRequest.POST("/articles/" + articleId + "/review-article", er);
        HttpResponse<ExpertReviewDTO> rsp = client.toBlocking().exchange(req, ExpertReviewDTO.class);
        ExpertReviewDTO rspER = rsp.body();
        assertEquals(CREATED, rsp.getStatus());
        assertNotNull(rspER);

        // Test that Expert Review 1 has the correct info
        List<ExpertReview> expertReviews = expertReviewRepository.findAll();
        assertEquals(1, expertReviews.size());

        Optional<ExpertReview> actual = expertReviewRepository.findByCommentPostId(rspER.getId());
        assertFalse(actual.isEmpty());
        assertEquals(er.getAuthor(), actual.get().getCommentPost().getUser().getUsername());
        assertEquals(er.getContent(), actual.get().getCommentPost().getContent());
        assertEquals(er.getStatus(), actual.get().getStatus());

        // -- User 2 posting Expert Review with 'Needs Work' status on an Article 1 with
        // Inline Comments
        List<HighlightSectionForm> hsForms = new ArrayList<>(1);
        hsForms.add(new HighlightSectionForm(12, 4, 20, 3));
        // Attach HighlightSections to InlineComment
        List<InlineCommentForm> icForms = new ArrayList<>(1);
        icForms.add(new InlineCommentForm(3, "selected content", "inline comment from user 2", hsForms));
        // Attach InlineComments to ExpertReview
        er = new ExpertReviewForm("User2", "content 2", "Needs Work", icForms);

        req = HttpRequest.POST("/articles/" + articleId + "/review-article", er);
        rsp = client.toBlocking().exchange(req, ExpertReviewDTO.class);
        rspER = rsp.body();
        assertEquals(CREATED, rsp.getStatus());
        assertNotNull(rspER);

        // Test that Expert Review 2 has correct info
        expertReviews = expertReviewRepository.findAll();
        assertEquals(2, expertReviews.size());

        assertEquals(er.getAuthor(), rspER.getAuthor().getUsername());
        assertEquals(er.getContent(), rspER.getContent());
        assertEquals(er.getStatus(), rspER.getStatus());

        InlineCommentDTO icFView = rspER.getInlineComments().get(0);
        assertEquals(icForms.get(0).getPageNum(), icFView.getPageNum());
        assertEquals(icForms.get(0).getSelectedContent(), icFView.getSelectedContent());
        assertEquals(icForms.get(0).getContent(), icFView.getContent());

        HighlightSectionDTO hsView = icFView.getHighlightSections().get(0);
        assertEquals(hsForms.get(0).getX(), hsView.getX());
        assertEquals(hsForms.get(0).getY(), hsView.getY());
        assertEquals(hsForms.get(0).getWidth(), hsView.getWidth());
        assertEquals(hsForms.get(0).getHeight(), hsView.getHeight());

        // -- User 3 posting Expert Review with 'Rejected' status on an Article 1
        er = new ExpertReviewForm("User3", "content 1", "Rejected", null);

        req = HttpRequest.POST("/articles/" + articleId + "/review-article", er);
        rsp = client.toBlocking().exchange(req, ExpertReviewDTO.class);
        rspER = rsp.body();
        assertEquals(CREATED, rsp.getStatus());
        assertNotNull(rspER);

        // Test that Expert Review 3 has correct info
        expertReviews = expertReviewRepository.findAll();
        assertEquals(3, expertReviews.size());

        actual = expertReviewRepository.findByCommentPostId(rspER.getId());
        assertFalse(actual.isEmpty());
        assertEquals(er.getAuthor(), actual.get().getCommentPost().getUser().getUsername());
        assertEquals(er.getContent(), actual.get().getCommentPost().getContent());
        assertEquals(er.getStatus(), actual.get().getStatus());
    }

    /**
     * Test POST endpoint for posting invalid Expert Review on Article post.
     */
    @Test
    public void testPostInvalidExpertReview() {
        Article a = articleRepository.findAll().get(0);
        ExpertReviewForm er = new ExpertReviewForm("User1", "content 1", "Approved", null);

        // -- Posting Expert Review on non-existent Article post
        HttpRequest<?> req;
        try {
            req = HttpRequest.POST("/articles/0/review-article", er);
            client.toBlocking().exchange(req, String.class);
            fail("Expert Review should not be inserted in non-existent Article.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }
        List<ExpertReview> expertReviews = expertReviewRepository.findAll();
        assertEquals(0, expertReviews.size());

        // -- Posting empty Expert Review
        er = new ExpertReviewForm("non-existent", "content", "Approved", null);
        try {
            req = HttpRequest.POST("/articles/" + a.getId() + "/review-article", er);
            client.toBlocking().exchange(req, String.class);
            fail("Expert Review by non-existent user should not be inserted.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }
        expertReviews = expertReviewRepository.findAll();
        assertEquals(0, expertReviews.size());

        // -- Posting empty Expert Review
        er = new ExpertReviewForm("User1", "", "Approved", null);
        try {
            req = HttpRequest.POST("/articles/" + a.getId() + "/review-article", er);
            client.toBlocking().exchange(req, String.class);
            fail("Empty Expert Review should not be inserted.");
        } catch (HttpClientResponseException e) {
            assertEquals("Unprocessable Entity", e.getMessage());
        }
        expertReviews = expertReviewRepository.findAll();
        assertEquals(0, expertReviews.size());

        // -- Posting Expert Review with invalid status
        er = new ExpertReviewForm("User1", "content", "Accepted", null);
        try {
            req = HttpRequest.POST("/articles/" + a.getId() + "/review-article", er);
            client.toBlocking().exchange(req, String.class);
            fail("Empty Expert Review should not be inserted.");
        } catch (HttpClientResponseException e) {
            assertEquals("Unprocessable Entity", e.getMessage());
        }
        expertReviews = expertReviewRepository.findAll();
        assertEquals(0, expertReviews.size());
    }

    /**
     * Test endpoint for getting all Expert Reviews from Article.
     */
    @Test
    public void testGetArticleExpertReviews() {
        Article a = articleRepository.findAll().get(0);

        // -- Get Expert Reviews from Article post with no Expert Reviews
        HttpRequest<?> req = HttpRequest.GET("/articles/" + a.getId() + "/expert-reviews");
        HttpResponse<List<ExpertReviewDTO>> rsp = client.toBlocking().exchange(req,
                Argument.listOf(ExpertReviewDTO.class));
        List<ExpertReviewDTO> expertReviews = rsp.body();

        assertEquals(OK, rsp.getStatus());
        assertNotNull(expertReviews);
        assertEquals(0, expertReviews.size());

        // -- Get Expert Reviews from Article with Expert Reviews
        List<HighlightSectionForm> hsForms = new ArrayList<>(1);
        hsForms.add(new HighlightSectionForm(1, 4, 2, 21));
        List<InlineCommentForm> icForms = new ArrayList<>(1);
        icForms.add(new InlineCommentForm(7, "selected content", "inline comment from user 1", hsForms));
        ExpertReviewForm er = new ExpertReviewForm("User1", "content 1", "Approved", icForms);
        expertReviewService.postExpertReview(a.getId(), er);

        req = HttpRequest.GET("/articles/" + a.getId() + "/expert-reviews");
        rsp = client.toBlocking().exchange(req, Argument.listOf(ExpertReviewDTO.class));
        expertReviews = rsp.body();

        assertEquals(OK, rsp.getStatus());
        assertNotNull(expertReviews);
        assertEquals(1, expertReviews.size());

        // Test that Expert Reviews has correct info
        assertEquals(1, expertReviews.size());

        assertEquals(er.getAuthor(), expertReviews.get(0).getAuthor().getUsername());
        assertEquals(er.getContent(), expertReviews.get(0).getContent());
        assertEquals(er.getStatus(), expertReviews.get(0).getStatus());
        assertEquals(0, expertReviews.get(0).getScore());

        InlineCommentDTO icFView = expertReviews.get(0).getInlineComments().get(0);
        assertEquals(icForms.get(0).getPageNum(), icFView.getPageNum());
        assertEquals(icForms.get(0).getSelectedContent(), icFView.getSelectedContent());
        assertEquals(icForms.get(0).getContent(), icFView.getContent());

        HighlightSectionDTO hsView = icFView.getHighlightSections().get(0);
        assertEquals(hsForms.get(0).getX(), hsView.getX());
        assertEquals(hsForms.get(0).getY(), hsView.getY());
        assertEquals(hsForms.get(0).getWidth(), hsView.getWidth());
        assertEquals(hsForms.get(0).getHeight(), hsView.getHeight());

        // -- Get Expert Reviews from non-existent Article post
        try {
            req = HttpRequest.GET("/articles/0/expert-reviews");
            client.toBlocking().exchange(req, String.class);
            fail("Article post should not exist.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }
    }

    /**
     * Test endpoint for getting all Expert Reviews from Article.
     */
    @Test
    public void testGetExpertReviewInfo() {
        Article a = articleRepository.findAll().get(0);

        // -- Get Expert Reviews 1
        ExpertReviewForm er = new ExpertReviewForm("User1", "content 1", "Approved", null);
        ExpertReviewDTO expected = expertReviewService.postExpertReview(a.getId(), er);

        HttpRequest<?> req = HttpRequest.GET("/expert-reviews/" + expected.getId());
        HttpResponse<ExpertReviewDTO> rsp = client.toBlocking().exchange(req, ExpertReviewDTO.class);
        ExpertReviewDTO expertReviews = rsp.body();

        assertEquals(OK, rsp.getStatus());
        assertNotNull(expertReviews);

        Optional<ExpertReview> actual = expertReviewRepository.findByCommentPostId(expected.getId());
        assertFalse(actual.isEmpty());
        assertEquals(expected.getContent(), actual.get().getCommentPost().getContent());
        assertEquals(expected.getStatus(), actual.get().getStatus());

        // -- Get Expert Reviews from non-existent Article post
        try {
            req = HttpRequest.GET("/expert-reviews/0");
            client.toBlocking().exchange(req, String.class);
            fail("Expert review should not exist.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }
    }

    /**
     * Test endpoint for getting all Expert Reviews from User.
     */
    @Test
    public void testGetUserExpertReviews() {
        User u = userRepository.findByUsername("User1").get();
        Article a = articleRepository.findAll().get(0);

        // -- Get Expert Reviews from User with no Expert Reviews
        HttpRequest<?> req = HttpRequest.GET("/users/" + u.getUsername() + "/expert-reviews");
        HttpResponse<List<UserReviewDTO>> rsp = client.toBlocking().exchange(req, Argument.listOf(UserReviewDTO.class));
        List<UserReviewDTO> expertReviews = rsp.body();

        assertEquals(OK, rsp.getStatus());
        assertNotNull(expertReviews);
        assertEquals(0, expertReviews.size());

        // -- Get Expert Reviews from User with Expert Reviews
        ExpertReviewForm er = new ExpertReviewForm("User1", "content 1", "Approved", null);
        expertReviewService.postExpertReview(a.getId(), er);

        req = HttpRequest.GET("/users/" + u.getUsername() + "/expert-reviews");
        rsp = client.toBlocking().exchange(req, Argument.listOf(UserReviewDTO.class));
        expertReviews = rsp.body();

        assertEquals(OK, rsp.getStatus());
        assertNotNull(expertReviews);
        assertEquals(1, expertReviews.size());

        // Test that Expert Reviews has correct info
        UserReviewDTO actual = expertReviews.get(0);
        assertEquals(u.getUsername(), actual.getAuthor().getUsername());
        assertEquals(a.getId(), actual.getArticleId());
        assertEquals(er.getContent(), actual.getContent());
        assertEquals(er.getStatus(), actual.getStatus());
        assertEquals(0, actual.getScore());

        // -- Get Expert Reviews from non-existent Article post
        try {
            req = HttpRequest.GET("/users/non-existent/expert-reviews");
            client.toBlocking().exchange(req, String.class);
            fail("User should not exist.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }
    }

}
