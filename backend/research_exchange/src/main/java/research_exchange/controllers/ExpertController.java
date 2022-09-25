package research_exchange.controllers;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.svm.core.annotate.Inject;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotBlank;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import research_exchange.repositories.ExpertRepository;
import research_exchange.repositories.UserRepository;
import research_exchange.models.Expert;
import research_exchange.models.ExpertRequest;
import research_exchange.models.User;
import research_exchange.repositories.ExpertRequestRepository;

@Controller("/experts")
@Secured(SecurityRule.IS_ANONYMOUS)
@ExecuteOn(TaskExecutors.IO)
public class ExpertController {

    @Inject
    ObjectStorageClient objectStorageClient;

    private final UserRepository userRepository;

    private final ExpertRepository expertRepository;

    private final ExpertRequestRepository expertRequestRepository;

    public ExpertController(UserRepository userRepository, ExpertRepository expertRepository,
            ExpertRequestRepository expertRequestRepository) {
        this.userRepository = userRepository;
        this.expertRepository = expertRepository;
        this.expertRequestRepository = expertRequestRepository;
    }

    @Secured({ "Admin" })
    @Get(value = "/")
    public List<Expert> getAllExperts() {
        return expertRepository.findAll();
    }

    @Secured({ "Admin" })
    @Put(value = "/", consumes = { "application/json" })
    public HttpResponse<?> assignExpert(@Body Expert expert) {
        Optional<User> user = userRepository.findByUsername(expert.getUsername());
        if (user.isPresent()) {
            user.get().setRole("Expert");
            userRepository.update(user.get());

            expertRepository.save(expert);

            Optional<ExpertRequest> expertRequest = expertRequestRepository.findByUsername(expert.getUsername());
            if (expertRequest.isPresent()) {
                expertRequestRepository.delete(expertRequest.get());
            }

            return HttpResponse.status(HttpStatus.OK).body(expert);
        } else {
            return HttpResponse.status(HttpStatus.CONFLICT);
        }
    }

    @Secured({ "Admin" })
    @Delete(value = "/{username}")
    public HttpResponse<?> removeExpert(@NotBlank String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            user.get().setRole("User");
            userRepository.update(user.get());

            Optional<Expert> expertOptional = expertRepository.findByUsername(username);
            if (expertOptional.isPresent()) {
                expertRepository.delete(expertOptional.get());
            }

            return HttpResponse.status(HttpStatus.OK);
        } else {
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        }
    }

}
