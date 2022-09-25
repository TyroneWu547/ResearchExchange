
package research_exchange.api;

import org.junit.jupiter.api.Test;

import io.micronaut.context.env.Environment;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import research_exchange.repositories.UserRepository;

@MicronautTest(environments = Environment.ORACLE_CLOUD)
public class APISignupTest {

    @Inject
    UserRepository userRepository;

    @Test
    public void testFindUser() {

    }

}
