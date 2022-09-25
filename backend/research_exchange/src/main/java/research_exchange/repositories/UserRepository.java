package research_exchange.repositories;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import research_exchange.models.User;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();

    Optional<User> findByUsername(String username);

    Optional<String> findUsernameById(Long id);

    Optional<Long> findIdByUsername(String username);

    Optional<String> findPasswordByUsername(String username);

}
