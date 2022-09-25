package research_exchange.models;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public class ExpertRequest {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String field;

    private String reason;

    public ExpertRequest(String username, String field, String reason) {
        this.username = username;
        this.field = field;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getField() {
        return field;
    }

    public String getReason() {
        return reason;
    }

}
