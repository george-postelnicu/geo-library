package ro.george.postelnicu.geolibrary.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.george.postelnicu.geolibrary.model.BookAuthorRelation;

public interface BookAuthorRelationRepository extends JpaRepository<BookAuthorRelation, Long> {
    boolean existsByAuthorId(@NotNull Long id);
}
