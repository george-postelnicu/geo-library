package ro.george.postelnicu.geolibrary.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.george.postelnicu.geolibrary.model.BookKeywordRelation;

public interface BookKeywordRelationRepository extends JpaRepository<BookKeywordRelation, Long> {
    boolean existsByKeywordId(@NotNull Long id);
}
