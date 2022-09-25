package research_exchange.repositories;

import research_exchange.models.HighlightSection;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

/**
 * The Highlight Section repository class which handles the necessary queries
 * related to the Highlight Section entity.
 * 
 * @author Tyrone Wu
 */
@JdbcRepository(dialect = Dialect.ORACLE)
public interface HighlightSectionRepository extends CrudRepository<HighlightSection, Long> {

}
