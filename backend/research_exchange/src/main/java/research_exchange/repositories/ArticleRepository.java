package research_exchange.repositories;

import java.util.List;
import java.util.Optional;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import research_exchange.models.Article;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface ArticleRepository extends CrudRepository<Article, Long> {

    @Override
    @NonNull
    List<Article> findAll();

    @NonNull
    List<Article> findAll(Pageable pageable);

    @Override
    Optional<Article> findById(Long id);

    Optional<String> findNameById(Long id);

    Optional<Integer> findApprovedById(Long id);

    List<Article> listOrderByScore();

    void update(@Id Long id, int score);

}
