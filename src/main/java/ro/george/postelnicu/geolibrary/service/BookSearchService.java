package ro.george.postelnicu.geolibrary.service;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.BookSearchCriteria;
import ro.george.postelnicu.geolibrary.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.data.jpa.domain.Specification.allOf;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static ro.george.postelnicu.geolibrary.specification.StringLikeFieldSpecification.buildSpecification;

@Service
public class BookSearchService {
    private final BookRepository repository;

    public BookSearchService(BookRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = REQUIRED, readOnly = true)
    public Page<Book> search(@NotNull @Valid BookSearchCriteria searchCriteria, @NotNull Pageable pageRequest) {
        List<Specification<Book>> specifications = new ArrayList<>();
        specifications.add(buildPublisherAndCover(searchCriteria));
        specifications.add(buildAuthors(searchCriteria));
        specifications.add(buildKeywords(searchCriteria));
        specifications.add(buildLanguages(searchCriteria));

        return repository.findAll(allOf(specifications), pageRequest);
    }

    private static Specification<Book> buildPublisherAndCover(BookSearchCriteria searchCriteria) {
        Specification<Book> publisherSpec = isNull(searchCriteria.getPublisher()) ? ignore()
                : buildSpecification(searchCriteria.getPublisher(), root -> root.get("publisher"));
        Specification<Book> coverTypeSpec = isNull(searchCriteria.getCoverType()) ? ignore()
                : buildSpecification(searchCriteria.getCoverType(), root -> root.get("cover"));

        return allOf(publisherSpec, coverTypeSpec);
    }

    private static Specification<Book> buildPublishYear(BookSearchCriteria searchCriteria) {
        if (isNull(searchCriteria.getMinYear()) && isNull(searchCriteria.getMaxYear())) {
            return ignore();
        }

        return (root, query, cb) -> {
            Predicate greaterThanOrEqualTo = null;
            Predicate lessThanOrEqualTo = null;
            if (nonNull(searchCriteria.getMinYear())) {
                greaterThanOrEqualTo = cb.greaterThanOrEqualTo(root.get("publishYear"), searchCriteria.getMinYear());
            }
            if (nonNull(searchCriteria.getMaxYear())) {
                lessThanOrEqualTo = cb.lessThanOrEqualTo(root.get("publishYear"), searchCriteria.getMaxYear());
            }

            return cb.and(greaterThanOrEqualTo, lessThanOrEqualTo);
        };
    }

    private static Specification<Book> buildAuthors(BookSearchCriteria searchCriteria) {
        if (searchCriteria.getAuthors().isEmpty()) {
            return ignore();
        }

        return ((root, query, cb) -> root.get("authors").get("name").in(searchCriteria.getAuthors()));
    }

    private static Specification<Book> buildKeywords(BookSearchCriteria searchCriteria) {
        if (searchCriteria.getKeywords().isEmpty()) {
            return ignore();
        }

        return ((root, query, cb) -> root.get("keywords").get("name").in(searchCriteria.getKeywords()));
    }

    private static Specification<Book> buildLanguages(BookSearchCriteria searchCriteria) {
        if (searchCriteria.getLanguages().isEmpty()) {
            return ignore();
        }

        return ((root, query, cb) -> root.get("languages").get("name").in(searchCriteria.getLanguages()));
    }

    private static <Book> Specification<Book> ignore() {
        return (root, query, cb) -> cb.conjunction();
    }
}
