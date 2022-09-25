package research_exchange.repositories;

import java.util.List;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import research_exchange.models.Tag;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface TagAbstractRepository extends CrudRepository<Tag, Long> {

    @Join("article")
    List<Tag> findByName(String name);

    @Join("article")
    List<Tag> findAllByArticleId(Long id);

}
