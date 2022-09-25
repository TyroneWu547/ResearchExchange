package research_exchange.repositories;

import java.util.List;
import java.util.Optional;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import research_exchange.models.ExpertRequest;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface ExpertRequestRepository extends CrudRepository<ExpertRequest, Long> {

    @Override
    @NonNull
    List<ExpertRequest> findAll();

    Optional<ExpertRequest> findByUsername(String username);

}
