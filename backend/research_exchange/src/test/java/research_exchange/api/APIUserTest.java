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
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import research_exchange.dto.UserInfoDTO;
import research_exchange.models.Expert;
import research_exchange.models.User;
import research_exchange.repositories.ExpertRepository;
import research_exchange.repositories.UserRepository;

@MicronautTest(environments = Environment.ORACLE_CLOUD)
public class APIUserTest {

    /** For accessing Article DB table */
    @Inject
    private UserRepository userRepository;
    /** For accessing Article DB table */
    @Inject
    private ExpertRepository expertRepository;

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
        ;

        // Add Articles and Tag to the database
        Expert e2 = new Expert("test", "Biology");
        expertRepository.save(e2);
        byte[] salt = "salt".getBytes();
        User u1 = new User("name", "test", "email", "password", salt, "expert");
        userRepository.save(u1);
    }

    /**
     * Test the GET endoints for Experts
     */
    @Test
    public void getAllUsers() {

        HttpRequest<?> getUsers = HttpRequest.GET("/users");
        HttpResponse<List<User>> rspOfGetExperts = client.toBlocking().exchange(getUsers, Argument.listOf(User.class));
        assertEquals(OK, rspOfGetExperts.getStatus());
    }

    @Test
    public void testUserInfo() {
        String username = "test";
        HttpRequest<?> getUsers = HttpRequest.GET("/users/" + username + "/info");
        HttpResponse<UserInfoDTO> rspOfGetExperts = client.toBlocking().exchange(getUsers, UserInfoDTO.class);
        assertEquals(OK, rspOfGetExperts.getStatus());
    }

    @Test
    public void testEditUser() {
        MultipartBody requestBody = MultipartBody.builder()
                .addPart("username", "test")
                .addPart("profilePicture", "hello.jpg", MediaType.IMAGE_JPEG_TYPE, "Hello".getBytes())
                .addPart("name", "name")
                .addPart("email", "email")
                .addPart("oldPassword", "password")
                .addPart("newPassword", "newPassword")
                .build();

        String username = "test";

        HttpRequest<?> editUser = HttpRequest.PUT("/users/" + username + "/edit-profile", requestBody);
        HttpResponse<HttpStatus> rsp = client.toBlocking().exchange(editUser, HttpStatus.class);
        assertEquals(OK, rsp.getStatus());
    }

}
