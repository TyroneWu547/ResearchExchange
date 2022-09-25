package research_exchange.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotBlank;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import research_exchange.models.ExpertRequest;
import research_exchange.models.User;
import research_exchange.repositories.ExpertRequestRepository;
import research_exchange.repositories.UserRepository;

@Controller("/expert-requests")
@Secured(SecurityRule.IS_ANONYMOUS)
@ExecuteOn(TaskExecutors.IO)
public class ExpertRequestController {

    @Inject
    ObjectStorageClient objectStorageClient;

    private final UserRepository userRepository;

    private final ExpertRequestRepository expertRequestRepository;

    public ExpertRequestController(UserRepository expertRepository, ExpertRequestRepository expertRequestRepository) {
        this.userRepository = expertRepository;
        this.expertRequestRepository = expertRequestRepository;
    }

    @Secured({ "Admin" })
    @Get(value = "/")
    public List<ExpertRequest> getAllExpertRequests() {
        return expertRequestRepository.findAll();
    }

    @Post(value = "/", consumes = { "application/json" })
    public HttpResponse<?> makeExpertRequest(@Body ExpertRequest expertRequest) {
        Optional<User> userOptional = userRepository.findByUsername(expertRequest.getUsername());
        if (userOptional.isPresent()) {
            expertRequestRepository.save(expertRequest);
            return HttpResponse.status(HttpStatus.OK).body(expertRequest);
        } else {
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({ "Admin" })
    @Delete(value = "/{username}")
    public HttpResponse<?> deleteExpertRequest(@NotBlank String username) {
        Optional<ExpertRequest> expertRequestOptional = expertRequestRepository.findByUsername(username);
        if (expertRequestOptional.isPresent()) {
            expertRequestRepository.delete(expertRequestOptional.get());
            return HttpResponse.status(HttpStatus.OK);
        } else {
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        }
    }

    @Get(value = "/{username}/has-requested")
    public boolean checkRequested(@NotBlank String username) {
        return expertRequestRepository.findByUsername(username).isPresent();
    }

}
