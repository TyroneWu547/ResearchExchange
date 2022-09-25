package research_exchange.api;

import static org.mockito.Mockito.mock;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

import io.micronaut.context.env.Environment;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import research_exchange.repositories.ArticleRepository;
import research_exchange.repositories.TagAbstractRepository;
import research_exchange.repositories.UserRepository;

@MicronautTest(environments = Environment.ORACLE_CLOUD)
public class JwtTest {
    @Inject
    private UserRepository userRepository;

    @Inject
    private ArticleRepository articleRepository;

    @Inject
    private TagAbstractRepository tagRepository;
    @Inject
    @Client("/")
    HttpClient client;

    // @Test
    // void accessingASecuredUrlWithoutAuthenticatingReturnsUnauthorized() {
    // HttpClientResponseException e =
    // assertThrows(HttpClientResponseException.class, () -> {
    // client.toBlocking().exchange(HttpRequest.GET("/").accept(TEXT_PLAIN));
    // });

    // assertEquals(UNAUTHORIZED, e.getStatus());
    // }

    // @Test
    // void uponSuccessfulAuthenticationAJsonWebTokenIsIssuedToTheUser() throws
    // ParseException {
    // UsernamePasswordCredentials creds = new
    // UsernamePasswordCredentials("username", "password");
    // HttpRequest<?> request = HttpRequest.POST("/login", creds);
    // HttpResponse<BearerAccessRefreshToken> rsp =
    // client.toBlocking().exchange(request, BearerAccessRefreshToken.class);
    // assertEquals(OK, rsp.getStatus());

    // BearerAccessRefreshToken bearerAccessRefreshToken = rsp.body();
    // assertEquals("username", bearerAccessRefreshToken.getUsername());
    // assertNotNull(bearerAccessRefreshToken.getAccessToken());
    // assertTrue(JWTParser.parse(bearerAccessRefreshToken.getAccessToken())
    // instanceof SignedJWT);
    // }

    @Test
    void successfulSignUp() throws ParseException {
        // User user = new User("test name", "username", "test@test.com", "password",
        // false);
        // HttpRequest<?> request = HttpRequest.POST("/signup", user);
        // HttpResponse<?> rsp = client.toBlocking().exchange(request,
        // HttpStatus.class);
        // System.out.println(rsp.getStatus());
        // assertEquals(OK, rsp.getStatus());

        // UsernamePasswordCredentials creds = new
        // UsernamePasswordCredentials("username", "password");
        // HttpRequest<?> loginRequest = HttpRequest.POST("/login", creds);
        // HttpResponse<BearerAccessRefreshToken> rspLogin =
        // client.toBlocking().exchange(loginRequest, BearerAccessRefreshToken.class);
        // BearerAccessRefreshToken bearerAccessRefreshToken = rspLogin.body();
        // assertEquals("username", bearerAccessRefreshToken.getUsername());
        // assertNotNull(bearerAccessRefreshToken.getAccessToken());
        // assertTrue(JWTParser.parse(bearerAccessRefreshToken.getAccessToken())
        // instanceof SignedJWT);
    }

    @Test
    public void testUserService() {
        // UserService userService = new UserService(userRepository);
        // User user = new User("test name", "username", "test@test.com", "password",
        // "role");
        // assertTrue(userService.createUser(user));
        // User duplicateUser = new User("test name", "username", "test@test.com",
        // "password");
        // assertFalse(userService.createUser(duplicateUser));
    }

    @MockBean(UserRepository.class)
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    }

}
