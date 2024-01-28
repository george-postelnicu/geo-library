package ro.george.postelnicu.geolibrary.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.BookSearchCriteria;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.data.jpa.domain.Specification.allOf;
import static ro.george.postelnicu.geolibrary.specification.StringLikeFieldSpecification.buildSpecification;
import static ro.george.postelnicu.geolibrary.util.StringUtil.isBlankOrWrongWildcard;

@Repository
public class BookSpecificationRepository {

    private final BookRepository repository;

    @Autowired
    public BookSpecificationRepository(BookRepository repository) {
        this.repository = repository;
    }

    public Page<Book> search(@NotNull @Valid BookSearchCriteria searchCriteria, @NotNull Pageable pageRequest) {
        List<Specification<Book>> specifications = new ArrayList<>();
        specifications.add(buildNameFullTitleAndDescription(searchCriteria));
        specifications.add(buildPublisherAndCover(searchCriteria));
        specifications.add(buildPublishYear(searchCriteria));
        specifications.add(buildPages(searchCriteria));
        specifications.add(buildAuthors(searchCriteria));
        specifications.add(buildKeywords(searchCriteria));
        specifications.add(buildLanguages(searchCriteria));

        return repository.findAll(allOf(specifications), pageRequest);
    }

    private static Specification<Book> buildNameFullTitleAndDescription(BookSearchCriteria searchCriteria) {
        Specification<Book> name = isBlankOrWrongWildcard(searchCriteria.name()) ? ignore()
                : buildSpecification(searchCriteria.name(), root -> root.get("name"));
        Specification<Book> fullTitle = isBlankOrWrongWildcard(searchCriteria.fullTitle()) ? ignore()
                : buildSpecification(searchCriteria.fullTitle(), root -> root.get("fullTitle"));
        Specification<Book> description = isBlankOrWrongWildcard(searchCriteria.description()) ? ignore()
                : buildSpecification(searchCriteria.description(), root -> root.get("description"));

        return allOf(name, fullTitle, description);
    }

    private static Specification<Book> buildPublisherAndCover(BookSearchCriteria searchCriteria) {
        Specification<Book> publisherSpec = isBlankOrWrongWildcard(searchCriteria.publisher()) ? ignore()
                : buildSpecification(searchCriteria.publisher(), root -> root.get("publisher"));
        Specification<Book> coverTypeSpec = isNull(searchCriteria.coverType()) ? ignore()
                : buildSpecification(searchCriteria.coverType(), root -> root.get("cover"));

        return allOf(publisherSpec, coverTypeSpec);
    }

    private static Specification<Book> buildPublishYear(BookSearchCriteria searchCriteria) {
        if (isNull(searchCriteria.minYear()) && isNull(searchCriteria.maxYear())) {
            return ignore();
        }

        return compareIntegers("publishYear", searchCriteria.minYear(), searchCriteria.maxYear());
    }

    private static Specification<Book> buildPages(BookSearchCriteria searchCriteria) {
        if (isNull(searchCriteria.minPages()) && isNull(searchCriteria.maxPages())) {
            return ignore();
        }

        return compareIntegers("pages", searchCriteria.minPages(), searchCriteria.maxPages());
    }

    private static Specification<Book> compareIntegers(String columnName, Integer min, Integer max) {
        return (root, _, cb) -> {
            Predicate greaterThanOrEqualTo = null;
            Predicate lessThanOrEqualTo = null;
            if (nonNull(min)) {
                greaterThanOrEqualTo = cb.greaterThanOrEqualTo(root.get(columnName), min);
            }
            if (nonNull(max)) {
                lessThanOrEqualTo = cb.lessThanOrEqualTo(root.get(columnName), max);
            }

            if (greaterThanOrEqualTo != null && lessThanOrEqualTo != null) {
                return cb.and(greaterThanOrEqualTo, lessThanOrEqualTo);
            } else if (greaterThanOrEqualTo != null) {
                return greaterThanOrEqualTo;
            } else  {
                return lessThanOrEqualTo;
            }
        };
    }

    private static Specification<Book> buildAuthors(BookSearchCriteria searchCriteria) {
        if (Objects.isNull(searchCriteria.authors()) || searchCriteria.authors().isEmpty()) {
            return ignore();
        }

        return compareCollections("authors", searchCriteria.authors());
    }

    private static Specification<Book> buildKeywords(BookSearchCriteria searchCriteria) {
        if (Objects.isNull(searchCriteria.keywords()) || searchCriteria.keywords().isEmpty()) {
            return ignore();
        }

        return compareCollections("keywords", searchCriteria.keywords());
    }

    private static Specification<Book> buildLanguages(BookSearchCriteria searchCriteria) {
        if (Objects.isNull(searchCriteria.languages()) || searchCriteria.languages().isEmpty()) {
            return ignore();
        }

        return compareCollections("languages", searchCriteria.languages());
    }

    private static Specification<Book> compareCollections(String columnName, Set<String> names) {
        return (Root<Book> root, CriteriaQuery<?> _, CriteriaBuilder cb) -> {

            // Create a separate predicate for each author
            Set<Predicate> predicates = new HashSet<>();
            for (String authorName : names) {
                predicates.add(cb.equal(root.join(columnName).get("name"), authorName));
            }

            // Combine all predicates using 'AND'
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <Book> Specification<Book> ignore() {
        return (_, _, cb) -> cb.conjunction();
    }
}
