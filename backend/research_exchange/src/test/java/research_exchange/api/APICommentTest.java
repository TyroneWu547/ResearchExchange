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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import research_exchange.forms.CommentForm;
import research_exchange.models.Article;
import research_exchange.models.GeneralComment;
import research_exchange.models.User;
import research_exchange.repositories.ArticleRepository;
import research_exchange.repositories.GeneralCommentRepository;
import research_exchange.repositories.UserRepository;
import research_exchange.services.CommentService;
import research_exchange.dto.FollowupCommentDTO;
import research_exchange.dto.TopLevelCommentDTO;
import research_exchange.dto.UserCommentDTO;

/**
 * API Test class for CommentController.
 *
 * @author Tyrone Wu
 */
@MicronautTest(environments = Environment.ORACLE_CLOUD, transactional = false, rollback = true)
public class APICommentTest {

    /** For accessing User DB table. */
    @Inject
    private UserRepository userRepository;

    /** For accessing Article DB table */
    @Inject
    private ArticleRepository articleRepository;

    /** For accessing Comment DB table. */
    @Inject
    private GeneralCommentRepository generalCommentRepository;

    /** Comment services for saving. */
    @Inject
    private CommentService commentService;

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
        generalCommentRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
        byte[] salt = "salt".getBytes();
        // Add User and Article to DB
        User u1 = new User("User 1", "User1", "username@gmail.com", "password", salt, "role");
        User u2 = new User("User 2", "User2", "username@gmail.com", "password", salt, "role");
        userRepository.saveAll(Arrays.asList(u1, u2));

        Article a1 = new Article("article 1", "content1", "mainfield1", "subfield1", "pdf1", "repo1", "data1");
        articleRepository.save(a1);
    }

    /**
     * Test POST endpoint for Commenting on Article post.
     */
    @Test
    public void testPostComment() {
        // -- User 1 Commenting on an Article 1
        Long articleId = articleRepository.findAll().get(0).getId();
        CommentForm c1 = new CommentForm(null, null, "User1", "content 1");
        HttpRequest<?> req = HttpRequest.POST("/articles/" + articleId + "/post-comment", c1);
        HttpResponse<TopLevelCommentDTO> rspTop = client.toBlocking().exchange(req, TopLevelCommentDTO.class);
        TopLevelCommentDTO rspCV = rspTop.body();
        assertEquals(CREATED, rspTop.getStatus());
        assertNotNull(rspCV);

        // Test that Comment 1 has the correct info
        List<GeneralComment> comments = generalCommentRepository.findAll();
        assertEquals(1, comments.size());

        Optional<GeneralComment> actual = generalCommentRepository.findByCommentPostId(rspCV.getId());
        assertFalse(actual.isEmpty());
        assertNull(actual.get().getReplyToId());
        assertEquals(c1.getAuthor(), actual.get().getCommentPost().getUser().getUsername());
        assertEquals(c1.getContent(), actual.get().getCommentPost().getContent());

        // -- Replying to a Comment
        CommentForm c2 = new CommentForm(rspCV.getId(), rspCV.getId(), "User2", "reply to User 1");
        req = HttpRequest.POST("/articles/" + articleId + "/post-comment", c2);
        HttpResponse<FollowupCommentDTO> rspFollowup = client.toBlocking().exchange(req, FollowupCommentDTO.class);
        FollowupCommentDTO rspFCV = rspFollowup.body();
        assertEquals(CREATED, rspFollowup.getStatus());
        assertNotNull(rspFCV);

        // Test that Comment 2 has correct info
        comments = generalCommentRepository.findAll();
        assertEquals(2, comments.size());

        actual = generalCommentRepository.findByCommentPostId(rspFCV.getId());
        assertFalse(actual.isEmpty());
        assertEquals(rspCV.getId(), actual.get().getReplyToId());
        assertEquals(c2.getAuthor(), actual.get().getCommentPost().getUser().getUsername());
        assertEquals(c2.getContent(), actual.get().getCommentPost().getContent());

        // -- Commenting on non-existent Article post
        try {
            req = HttpRequest.POST("/articles/0/post-comment", c1);
            client.toBlocking().exchange(req, String.class);
            fail("Comment should not be inserted in non-existent Article.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }
        assertEquals(2, generalCommentRepository.findAll().size());

        // -- Commenting with non-existent User
        c1 = new CommentForm(null, null, "non-existent User", "content");
        try {
            req = HttpRequest.POST("/articles/" + articleId + "/post-comment", c1);
            client.toBlocking().exchange(req, String.class);
            fail("Comment with non-existent User should not be inserted.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }
        assertEquals(2, generalCommentRepository.findAll().size());

        // -- Following up to non-existent Comment
        c2 = new CommentForm(0L, 0L, "User2", "content");
        try {
            req = HttpRequest.POST("/articles/" + articleId + "/post-comment", c2);
            client.toBlocking().exchange(req, String.class);
            fail("Followup non-existent parent/thread should not be inserted.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }
        assertEquals(2, generalCommentRepository.findAll().size());

        // -- Commenting with no content
        c1 = new CommentForm(null, null, "User1", "");
        try {
            req = HttpRequest.POST("/articles/" + articleId + "/post-comment", c1);
            client.toBlocking().exchange(req, String.class);
            fail("Comment with empty content should not be inserted.");
        } catch (HttpClientResponseException e) {
            assertEquals("Unprocessable Entity", e.getMessage());
        }
        assertEquals(2, generalCommentRepository.findAll().size());
    }

    /**
     * Test GET endpoint for retrieving all Comments from an Article post.
     */
    @Test
    public void testGetArticleAllComments() {
        // -- Get Comments from Article post with no Comments
        Article a = articleRepository.findAll().get(0);
        HttpRequest<?> req = HttpRequest.GET("/articles/" + a.getId() + "/comments");
        HttpResponse<List<TopLevelCommentDTO>> rsp = client.toBlocking().exchange(req,
                Argument.listOf(TopLevelCommentDTO.class));
        List<TopLevelCommentDTO> comments = rsp.body();
        assertNotNull(comments);
        assertEquals(OK, rsp.getStatus());
        assertEquals(0, comments.size());

        // Generate and save Comments into database
        CommentForm c1 = new CommentForm(null, null, "User1", "content 1");
        TopLevelCommentDTO tlc = (TopLevelCommentDTO) commentService.postComment(a.getId(), c1);
        CommentForm c2 = new CommentForm(tlc.getId(), tlc.getId(), "User2", "reply to User 1");
        FollowupCommentDTO fc = (FollowupCommentDTO) commentService.postComment(a.getId(), c2);

        // -- Get Comments from Article post with Comments
        req = HttpRequest.GET("/articles/" + a.getId() + "/comments");
        rsp = client.toBlocking().exchange(req, Argument.listOf(TopLevelCommentDTO.class));
        comments = rsp.body();
        assertNotNull(comments);
        assertEquals(OK, rsp.getStatus());
        assertEquals(1, comments.size());

        // Test that top-level Comments have the correct info
        assertEquals(tlc.getAuthor().getUsername(), comments.get(0).getAuthor().getUsername());
        assertEquals(tlc.getContent(), comments.get(0).getContent());
        assertEquals(1, comments.get(0).getFollowups().size());

        assertEquals(fc.getAuthor().getUsername(), comments.get(0).getFollowups().get(0).getAuthor().getUsername());
        assertEquals(fc.getContent(), comments.get(0).getFollowups().get(0).getContent());
        assertEquals(tlc.getId(), comments.get(0).getFollowups().get(0).getReplyingTo());

        // -- Get Comments from non-existent Article post
        try {
            req = HttpRequest.GET("/articles/0/comments");
            client.toBlocking().exchange(req, String.class);
            fail("Article post should not exist.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }
    }

    /**
     * Test GET endpoint for retrieving all Comments from a User.
     */
    @Test
    public void testGetUserAllComments() {
        // -- Get Comments from User with no Comments
        User u1 = userRepository.findByUsername("User1").get();
        HttpRequest<?> req = HttpRequest.GET("/users/" + u1.getUsername() + "/comments");
        HttpResponse<List<UserCommentDTO>> rsp = client.toBlocking().exchange(req,
                Argument.listOf(UserCommentDTO.class));
        List<UserCommentDTO> comments = rsp.body();
        assertNotNull(comments);
        assertEquals(OK, rsp.getStatus());
        assertEquals(0, comments.size());

        // Generate and save Comment into database from User 1
        Article a = articleRepository.findAll().get(0);
        CommentForm c1 = new CommentForm(null, null, "User1", "content 1");
        commentService.postComment(a.getId(), c1);

        // -- Get Comments from User 1
        req = HttpRequest.GET("/users/" + u1.getUsername() + "/comments");
        rsp = client.toBlocking().exchange(req, Argument.listOf(UserCommentDTO.class));
        comments = rsp.body();
        assertNotNull(comments);
        assertEquals(OK, rsp.getStatus());
        assertEquals(1, comments.size());

        // Test User 1 has correct Comment info
        assertEquals(c1.getContent(), comments.get(0).getContent());
        assertEquals(a.getId(), comments.get(0).getArticleId());
        assertEquals(a.getName(), comments.get(0).getArticleTitle());

        // -- Get Comments from non-existent User
        try {
            req = HttpRequest.GET("/users/non-existent/comments");
            client.toBlocking().exchange(req, String.class);
            fail("User should not exist.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }
    }

    /**
     * Test POST endpoint for upvoting/downvoting a Comment.
     */
    @Test
    public void testVoteComment() {
        // Generate and save Comment 1 into database
        Article a = articleRepository.findAll().get(0);
        CommentForm c1 = new CommentForm(null, null, "User1", "content 1");
        TopLevelCommentDTO comment = (TopLevelCommentDTO) commentService.postComment(a.getId(), c1);
        assertEquals((Integer) 0, comment.getScore());

        // -- Upvote Comment
        HttpRequest<?> req = HttpRequest.POST("/comment-posts/" + comment.getId() + "/vote", "up");
        HttpResponse<Integer> rsp = client.toBlocking().exchange(req, Integer.class);
        Integer actual = rsp.body();
        assertNotNull(actual);
        assertEquals(OK, rsp.getStatus());
        assertEquals(1, actual);

        // -- Downvote Comment twice
        req = HttpRequest.POST("/comment-posts/" + comment.getId() + "/vote", "down");
        client.toBlocking().exchange(req);
        rsp = client.toBlocking().exchange(req, Integer.class);
        actual = rsp.body();
        assertNotNull(actual);
        assertEquals(OK, rsp.getStatus());
        assertEquals(-1, actual);

        // -- Voting on non-existent Comment
        try {
            req = HttpRequest.POST("/comment-posts/0/vote", "up");
            client.toBlocking().exchange(req, String.class);
            fail("Comment should not exist.");
        } catch (HttpClientResponseException e) {
            assertEquals("Not Found", e.getMessage());
        }

        // -- Voting on non-existent Comment
        try {
            req = HttpRequest.POST("/comment-posts/" + comment.getId() + "/vote", "upvote");
            client.toBlocking().exchange(req, String.class);
            fail("Comment should not exist.");
        } catch (HttpClientResponseException e) {
            assertEquals("Bad Request", e.getMessage());
        }
    }

}
