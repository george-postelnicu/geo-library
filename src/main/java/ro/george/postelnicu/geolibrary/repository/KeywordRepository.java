package ro.george.postelnicu.geolibrary.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.george.postelnicu.geolibrary.model.Keyword;

import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    boolean existsByNameIgnoreCase(@NotBlank String name);
    Optional<Keyword> findByNameIgnoreCase(@NotBlank String name);
}
