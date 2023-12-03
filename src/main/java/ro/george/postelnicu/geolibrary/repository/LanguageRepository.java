package ro.george.postelnicu.geolibrary.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.george.postelnicu.geolibrary.model.Author;
import ro.george.postelnicu.geolibrary.model.Language;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    boolean existsByNameIgnoreCase(@NotBlank String name);
    Optional<Language> findByNameIgnoreCase(@NotBlank String name);
}
