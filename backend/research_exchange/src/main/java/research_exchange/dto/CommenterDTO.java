package research_exchange.dto;

import research_exchange.models.User;

public class CommenterDTO {

    private String name;

    private String username;

    private String role;

    public CommenterDTO(User user) {
        setName(user.getName());
        setUsername(user.getUsername());
        setRole(user.getRole());
    }

    public CommenterDTO(String username) {
        setUsername(username);
    }

    public CommenterDTO(String username, String name) {
        setUsername(username);
        setName(name);
    }

    public CommenterDTO() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
