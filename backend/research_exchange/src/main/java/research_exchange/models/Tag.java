package research_exchange.models;

import io.github.resilience4j.core.lang.Nullable;
import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;

@MappedEntity
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relation(Kind.MANY_TO_ONE)
    @Nullable
    private Article article;

    @Creator
    public Tag(String name, @Nullable Article article) {
        this.name = name;
        this.article = article;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setArticle(@Nullable Article article) {
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }

}
