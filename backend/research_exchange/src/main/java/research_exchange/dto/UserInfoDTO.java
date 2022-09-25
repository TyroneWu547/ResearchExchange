package research_exchange.dto;

import io.micronaut.core.annotation.Nullable;

public class UserInfoDTO {

    @Nullable
    private String profilePictureUrl;

    private String name;

    private String username;

    private String email;

    private String role;

    public UserInfoDTO(String profilePictureUrl, String name, String username, String email, String role) {
        this.profilePictureUrl = profilePictureUrl;
        this.name = name;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public UserInfoDTO() {

    }

    public String getRole() {
        return role;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
