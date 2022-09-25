package research_exchange.authentication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.reactivestreams.Publisher;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import research_exchange.models.User;
import research_exchange.repositories.UserRepository;
import research_exchange.services.UserService;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

    private final UserRepository userRepository;

    private final UserService userService;

    public AuthenticationProviderUserPassword(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest,
            AuthenticationRequest<?, ?> authRequest) {

        final String username = authRequest.getIdentity().toString();
        final String password = authRequest.getSecret().toString();

        Optional<User> existingUser = userRepository.findByUsername(username);

        return Flux.create(emitter -> {
            if (existingUser.isPresent()
                    && userService.verifyPassword(username, password, existingUser.get().getSalt())) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("userId", existingUser.get().getId());

                String role = existingUser.get().getRole();

                emitter.next(AuthenticationResponse.success((String) authRequest.getIdentity(), Arrays.asList(role),
                        attributes));
                emitter.complete();
            } else {
                emitter.error(AuthenticationResponse.exception());
            }
        }, FluxSink.OverflowStrategy.ERROR);
    }

}
