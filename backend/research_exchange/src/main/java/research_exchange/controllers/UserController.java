package research_exchange.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import research_exchange.dto.UserInfoDTO;
import research_exchange.models.User;
import research_exchange.repositories.ExpertRepository;
import research_exchange.repositories.UserRepository;
import research_exchange.services.UserService;

@Controller("/users")
@Secured(SecurityRule.IS_ANONYMOUS)
@ExecuteOn(TaskExecutors.IO)
public class UserController {

    @Inject
    ObjectStorageClient objectStorageClient;

    private final UserRepository userRepository;

    private final ExpertRepository expertRepository;

    private final UserService userService;

    public UserController(UserRepository userRepository, ExpertRepository expertRepository, UserService userService) {
        this.userRepository = userRepository;
        this.expertRepository = expertRepository;
        this.userService = userService;
    }

    @Get(value = "/{username}/info")
    public HttpResponse<UserInfoDTO> getUserInfo(@NotBlank String username) {
        Optional<User> userOption = userRepository.findByUsername(username);
        if (userOption.isEmpty()) {
            return HttpResponse.notFound();
        }
        User user = userOption.get();
        String role = user.getRole();
        if (role.equals("Expert")) {
            role += " in " + expertRepository.findByUsername(username).get().getField();
        }
        return HttpResponse.ok(new UserInfoDTO(user.getProfilePictureUrl(), user.getName(), user.getUsername(),
                user.getEmail(), role));
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Put(value = "/{username}/edit-profile", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<?> editProfile(Authentication authentication, @NotBlank String username,
            @Nullable CompletedFileUpload profilePicture,
            String name, String email, String oldPassword, String newPassword) {

        if (!authentication.getName().equals(username)) {
            return HttpResponse.unauthorized();
        }

        Optional<User> userOption = userRepository.findByUsername(username);
        if (!userOption.isPresent()) {
            return HttpResponse.notFound();
        }
        User user = userOption.get();
        String hashed = userService.hashPassword(oldPassword, user.getSalt());
        if (!newPassword.equals("") && !hashed.equals(user.getPassword())) {
            return HttpResponse.badRequest("The oldPassword did not match user's password");
        }

        if (profilePicture != null) {
            final String bucketName = "research-exchange-pdf-storage-bucket";
            final String namespaceName = "idvpzhveofap";

            final String filename = profilePicture.getFilename();

            if (filename.length() < 4 || !filename.substring(filename.length() - 4).equals(".png")
                    && !filename.substring(filename.length() - 4).equals(".jpg")) {
                return HttpResponse.badRequest("Filename should end with .png or .jpg");
            }

            final String randomUuid = UUID.randomUUID().toString();
            final String extension = filename.substring(filename.length() - 4);
            final String objectName = filename.substring(0, filename.length() - 4) + "-" + randomUuid + extension;

            UploadConfiguration uploadConfiguration = UploadConfiguration.builder()
                    .allowMultipartUploads(true)
                    .allowParallelUploads(true)
                    .build();

            UploadManager uploadManager = new UploadManager(objectStorageClient, uploadConfiguration);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucketName(bucketName)
                    .namespaceName(namespaceName)
                    .objectName(objectName)
                    .build();

            InputStream stream = null;
            try {
                stream = profilePicture.getInputStream();
            } catch (IOException ex) {
                return HttpResponse.badRequest("File was unable to be read");
            }

            UploadRequest uploadDetails = UploadRequest.builder(stream, profilePicture.getSize()).allowOverwrite(true)
                    .build(request);

            uploadManager.upload(uploadDetails);

            final String picUrl = "https://objectstorage.us-ashburn-1.oraclecloud.com/n/" + namespaceName + "/b/"
                    + bucketName
                    + "/o/" + objectName;

            user.setProfilePictureUrl(picUrl);
        }

        user.setName(name);
        user.setEmail(email);

        if (!newPassword.equals("")) {
            String newHashed = userService.hashPassword(newPassword, user.getSalt());
            user.setPassword(newHashed);
        }

        userRepository.update(user);
        return HttpResponse.ok();
    }

}
