package research_exchange.models;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;

@MappedEntity
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @Relation(Kind.MANY_TO_ONE)
    private Article article;

    @Creator
    public Author(String username, Article article) {
        this.username = username;
        this.article = article;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }

}
