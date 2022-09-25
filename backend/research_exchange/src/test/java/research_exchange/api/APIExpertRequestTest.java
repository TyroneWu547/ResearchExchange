package research_exchange.api;

import static io.micronaut.http.HttpStatus.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micronaut.context.env.Environment;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import research_exchange.models.ExpertRequest;
import research_exchange.models.User;
import research_exchange.repositories.ExpertRequestRepository;
import research_exchange.repositories.UserRepository;

@MicronautTest(environments = Environment.ORACLE_CLOUD)
public class APIExpertRequestTest {

    /** For accessing User DB table */
    @Inject
    private UserRepository userRepository;

    /** For accessing ExpertRequest DB table */
    @Inject
    private ExpertRequestRepository expertRequestRepository;

    /** Injects root endpoint as http client. */
    @Inject
    @Client("/")
    HttpClient client;

    /**
     * Clears database and initializes Tag and Article.
     */
    @BeforeEach
    public void setup() {
        // Clear database
        userRepository.deleteAll();
        expertRequestRepository.deleteAll();

        byte[] salt = "salt".getBytes();
        User u1 = new User("name", "test", "email", "password", salt, "role");
        userRepository.save(u1);

        ExpertRequest er1 = new ExpertRequest("test", "Computer Science", "reason");
        expertRequestRepository.save(er1);
    }

    /**
     * Test the GET endoints for Experts
     */
    @Test
    public void getAllExperts() {
        HttpRequest<?> getExperts = HttpRequest.GET("/expert-requests/");
        HttpResponse<List<ExpertRequest>> rspOfGetExperts = client.toBlocking().exchange(getExperts,
                Argument.listOf(ExpertRequest.class));
        assertEquals(OK, rspOfGetExperts.getStatus());
    }

    /**
     * Test POST endpoint for adding an article.
     */
    @Test
    public void testMakeExpertRequest() {
        ExpertRequest tempExpertRequest = new ExpertRequest("test", "Computer Science", "reason");
        HttpRequest<?> postExperts = HttpRequest.POST("/expert-requests/", tempExpertRequest);
        HttpResponse<ExpertRequest> rspOfPostExperts = client.toBlocking().exchange(postExperts, ExpertRequest.class);
        assertEquals(OK, rspOfPostExperts.getStatus());
    }

    @Test
    public void testDeleteExpertRequest() {
        String username = expertRequestRepository.findAll().get(0).getUsername();
        HttpRequest<?> deleteExpertRequest = HttpRequest.DELETE("/expert-requests/" + username);
        HttpResponse<HttpStatus> rspOfDeleteExpertRequest = client.toBlocking().exchange(deleteExpertRequest,
                HttpStatus.class);
        assertEquals(OK, rspOfDeleteExpertRequest.getStatus());
    }

    @Test
    public void testCheckRequested() {
        String username = expertRequestRepository.findAll().get(0).getUsername();
        HttpRequest<?> getExpertRequest = HttpRequest.GET("/expert-requests/" + username + "/has-requested");
        HttpResponse<Boolean> rspOfGetExpertRequest = client.toBlocking().exchange(getExpertRequest, Boolean.class);
        assertEquals(OK, rspOfGetExpertRequest.getStatus());
    }

}
