package research_exchange.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import research_exchange.forms.UserForm;
import research_exchange.services.UserService;

@Controller("/signup")
@Secured(SecurityRule.IS_ANONYMOUS)
@ExecuteOn(TaskExecutors.IO)
public class SignupController {

    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @Post
    public HttpResponse<?> signup(@Body UserForm userForm) {
        if (userService.createUser(userForm)) {
            return HttpResponse.ok();
        } else {
            return HttpResponse.status(HttpStatus.CONFLICT);
        }
    }

}
