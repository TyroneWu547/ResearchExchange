package research_exchange.models;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public class Expert {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String field;

    public Expert(String username, String field) {
        this.username = username;
        this.field = field;
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

}
