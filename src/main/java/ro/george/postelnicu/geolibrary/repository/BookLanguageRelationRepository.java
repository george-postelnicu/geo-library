package ro.george.postelnicu.geolibrary.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.george.postelnicu.geolibrary.model.BookLanguageRelation;

public interface BookLanguageRelationRepository extends JpaRepository<BookLanguageRelation, Long> {
    boolean existsByLanguageId(@NotNull Long id);
}
