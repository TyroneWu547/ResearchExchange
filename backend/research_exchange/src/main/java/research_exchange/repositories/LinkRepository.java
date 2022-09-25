package research_exchange.repositories;

import java.util.List;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import research_exchange.models.Link;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface LinkRepository extends CrudRepository<Link, Long> {

    @Join("article")
    List<Link> findAllByArticleId(Long id);

}
