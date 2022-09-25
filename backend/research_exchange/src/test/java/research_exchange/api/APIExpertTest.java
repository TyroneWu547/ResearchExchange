package research_exchange.api;

import static io.micronaut.http.HttpStatus.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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
import research_exchange.models.Expert;
import research_exchange.models.ExpertRequest;
import research_exchange.models.User;
import research_exchange.repositories.ExpertRepository;
import research_exchange.repositories.ExpertRequestRepository;
import research_exchange.repositories.UserRepository;

@MicronautTest(environments = Environment.ORACLE_CLOUD)
public class APIExpertTest {

    /** For accessing Article DB table */
    @Inject
    private UserRepository userRepository;
    /** For accessing Article DB table */
    @Inject
    private ExpertRepository expertRepository;
    /** For accessing Article DB table */
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
        expertRepository.deleteAll();
        expertRequestRepository.deleteAll();
        ;

        // Add Articles and Tag to the database
        Expert e1 = new Expert("username", "Computer Science");
        Expert e2 = new Expert("test", "Biology");
        expertRepository.save(e1);
        expertRepository.save(e2);
        byte[] salt = "salt".getBytes();
        User u1 = new User("name", "test", "email", "password", salt, "expert");
        userRepository.save(u1);

        ExpertRequest er1 = new ExpertRequest("test", "Computer Science", "reason");
        expertRequestRepository.save(er1);
    }

    /**
     * Test the GET endoints for Experts
     */
    @Test
    public void getAllExperts() {

        HttpRequest<?> getExperts = HttpRequest.GET("/experts/");
        HttpResponse<List<Expert>> rspOfGetExperts = client.toBlocking().exchange(getExperts,
                Argument.listOf(Expert.class));
        assertEquals(OK, rspOfGetExperts.getStatus());
    }

    /**
     * Test PUT endpoint for adding an article.
     */
    @Test
    public void testAssignExpert() {
        Expert tempExpert = new Expert("test", "Computer Science");
        HttpRequest<?> putExperts = HttpRequest.PUT("/experts/", tempExpert);
        HttpResponse<?> rspOfPutExperts = client.toBlocking().exchange(putExperts, HttpResponse.class);
        assertEquals(OK, rspOfPutExperts.getStatus());

    }

    @Test
    public void testDeleteExpert() {
        HttpRequest<?> deleteExpert = HttpRequest.DELETE("/experts/test");
        HttpResponse<?> rspOfDeleteExperts = client.toBlocking().exchange(deleteExpert, HttpResponse.class);
        assertEquals(OK, rspOfDeleteExperts.getStatus());
    }

}
