package research_exchange.api;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpRequest;

import io.micronaut.context.env.Environment;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import static io.micronaut.http.HttpStatus.OK;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import research_exchange.models.Article;
import research_exchange.models.Author;
import research_exchange.models.Tag;
import research_exchange.models.User;
import research_exchange.repositories.ArticleRepository;
import research_exchange.repositories.AuthorRepository;
import research_exchange.repositories.TagAbstractRepository;
import research_exchange.repositories.UserRepository;

@MicronautTest(environments = Environment.ORACLE_CLOUD)
public class APIArticleTest {

    /** For accessing Article DB table */
    @Inject
    private ArticleRepository articleRepository;

    /** For accessing Tag DB table */
    @Inject
    private TagAbstractRepository tagRepository;

    @Inject
    private AuthorRepository authorRepository;

    @Inject
    private UserRepository userRepository;

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
        articleRepository.deleteAll();
        tagRepository.deleteAll();
        authorRepository.deleteAll();
        userRepository.deleteAll();

        // Add Articles and Tag to the database
        Article a1 = new Article("article1", "Artificial Intelligence content", "mainfield", "subfield", "link", "link",
                "link");
        Article saved1 = articleRepository.save(a1);
        Article a2 = new Article("article2", "Computer Science content", "mainfield", "subfield", "link", "link",
                "link");
        articleRepository.save(a2);

        User u1 = new User("name", "username", "email", "password", "salt".getBytes(), "user");
        Author author = new Author("username", saved1);
        authorRepository.save(author);
        userRepository.save(u1);

        Tag t = new Tag("AI", a1);
        tagRepository.save(t);
    }

    /**
     * Test the GET endoints for Articles
     */
    @Test
    public void getArticle() {
        // Get article by id
        Long id = articleRepository.findAll().get(0).getId();
        HttpRequest<?> getArticle = HttpRequest.GET("/articles/" + id);
        HttpResponse<Article> rspOfGetArticle = client.toBlocking().exchange(getArticle, Article.class);
        assertEquals(OK, rspOfGetArticle.getStatus());

        // Test get article with no query parameter
        HttpRequest<?> viewArticle = HttpRequest.GET("/articles");
        HttpResponse<List<Article>> rspOfviewArticle = client.toBlocking().exchange(viewArticle,
                Argument.listOf(Article.class));
        assertEquals(OK, rspOfviewArticle.getStatus());
    }

    /**
     * Test POST endpoint for adding an article.
     */
    @Test
    public void addArticle() {
        // Create multipart request body
        MultipartBody requestBody = MultipartBody.builder()
                .addPart("articlePdf", "hello.pdf", MediaType.TEXT_PLAIN_TYPE, "Hello".getBytes())
                .addPart("title", "title")
                .addPart("articleAbstract", "abstract")
                .addPart("authors", "authors")
                .addPart("tags", "tags")
                .addPart("mainField", "mainField")
                .addPart("subField", "subField")
                .addPart("repoUrl", "repoUrl")
                .addPart("dataUrl", "dataUrl")
                .addPart("extraLinks", "extraLinks")
                .build();

        HttpRequest<?> addArticle = HttpRequest.POST("/articles/", requestBody)
                .contentType(MediaType.MULTIPART_FORM_DATA_TYPE);
        HttpResponse<String> rspOfGetArticle = client.toBlocking().exchange(addArticle, String.class);
        assertEquals(OK, rspOfGetArticle.getStatus());
    }

    @Test
    public void numArticle() {
        HttpRequest<?> getArticle = HttpRequest.GET("/articles/numTotal");
        HttpResponse<Long> rspOfGetArticle = client.toBlocking().exchange(getArticle, Long.class);
        assertEquals(OK, rspOfGetArticle.getStatus());
        assertEquals(articleRepository.count(), rspOfGetArticle.body());
    }

    @Test
    public void viewApprovedArticlesForUser() {
        String username = "username";
        HttpRequest<?> getArticle = HttpRequest.GET("/articles/" + username + "/approved");
        HttpRequest.POST("/login", new UsernamePasswordCredentials("a", "b"));
        HttpResponse<List<Article>> rspOfGetArticle = client.toBlocking().exchange(getArticle,
                Argument.listOf(Article.class));
        assertEquals(OK, rspOfGetArticle.getStatus());
    }

    @Test
    public void viewUnapprovedArticlesForUser() {
        String username = "username";
        HttpRequest<?> getArticle = HttpRequest.GET("/articles/" + username + "/unapproved");
        HttpResponse<List<Article>> rspOfGetArticle = client.toBlocking().exchange(getArticle,
                Argument.listOf(Article.class));
        assertEquals(OK, rspOfGetArticle.getStatus());
    }

    @Test
    public void voteArticle() {
        Long id = articleRepository.findAll().get(0).getId();
        HttpRequest<?> voteArticle = HttpRequest.POST("/articles/" + id + "/vote", "up");
        HttpResponse<?> rspOfVoteArticle = client.toBlocking().exchange(voteArticle, HttpResponse.class);
        assertEquals(OK, rspOfVoteArticle.getStatus());
    }

    @Test
    public void testGetTopArticle() {
        HttpRequest<?> topArticle = HttpRequest.GET("/articles/top");
        HttpResponse<List<Article>> rspOfTopArticle = client.toBlocking().exchange(topArticle,
                Argument.listOf(Article.class));
        assertEquals(OK, rspOfTopArticle.getStatus());
    }

}
