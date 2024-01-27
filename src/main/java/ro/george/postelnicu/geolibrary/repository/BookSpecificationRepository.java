package ro.george.postelnicu.geolibrary.repository;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.BookSearchCriteria;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.data.jpa.domain.Specification.allOf;
import static ro.george.postelnicu.geolibrary.specification.StringLikeFieldSpecification.buildSpecification;

@Repository
public class BookSpecificationRepository {

    private final BookRepository repository;

    @Autowired
    public BookSpecificationRepository(BookRepository repository) {
        this.repository = repository;
    }

    public Page<Book> search(@NotNull @Valid BookSearchCriteria searchCriteria, @NotNull Pageable pageRequest) {
        List<Specification<Book>> specifications = new ArrayList<>();
        specifications.add(buildPublisherAndCover(searchCriteria));
        specifications.add(buildPublishYear(searchCriteria));
        specifications.add(buildPages(searchCriteria));
//        specifications.add(buildAuthors(searchCriteria));
//        specifications.add(buildKeywords(searchCriteria));
//        specifications.add(buildLanguages(searchCriteria));

        return repository.findAll(allOf(specifications), pageRequest);
    }

    private static Specification<Book> buildPublisherAndCover(BookSearchCriteria searchCriteria) {
        Specification<Book> publisherSpec = isNull(searchCriteria.publisher()) ? ignore()
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
            } else if (lessThanOrEqualTo != null) {
                return lessThanOrEqualTo;
            } else {
                return cb.conjunction();
            }
        };
    }

    private static Specification<Book> buildAuthors(BookSearchCriteria searchCriteria) {
        if (searchCriteria.authors().isEmpty()) {
            return ignore();
        }

        return ((root, query, cb) -> root.get("authors").get("name").in(searchCriteria.authors()));
    }

    private static Specification<Book> buildKeywords(BookSearchCriteria searchCriteria) {
        if (searchCriteria.keywords().isEmpty()) {
            return ignore();
        }

        return ((root, query, cb) -> root.get("keywords").get("name").in(searchCriteria.keywords()));
    }

    private static Specification<Book> buildLanguages(BookSearchCriteria searchCriteria) {
        if (searchCriteria.languages().isEmpty()) {
            return ignore();
        }

        return ((root, query, cb) -> root.get("languages").get("name").in(searchCriteria.languages()));
    }

    private static <Book> Specification<Book> ignore() {
        return (root, query, cb) -> cb.conjunction();
    }
}
