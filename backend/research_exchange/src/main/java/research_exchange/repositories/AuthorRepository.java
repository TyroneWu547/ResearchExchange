package research_exchange.repositories;

import java.util.List;

import javax.validation.constraints.NotBlank;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import research_exchange.models.Author;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface AuthorRepository extends CrudRepository<Author, Long> {

    @Join("article")
    List<Author> findAllByArticleId(Long id);

    @Join("article")
    List<Author> findByUsername(@NotBlank String name);

}
