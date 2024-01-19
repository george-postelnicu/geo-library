package ro.george.postelnicu.geolibrary.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.george.postelnicu.geolibrary.model.Author;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    boolean existsByNameIgnoreCase(@NotBlank String name);

    boolean existsByNameIgnoreCaseAndIdIsNot(@NotBlank String name, @NotNull long id);

    Optional<Author> findByNameIgnoreCase(@NotBlank String name);
}
