package ro.george.postelnicu.geolibrary.repository;

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

import static org.springframework.data.jpa.domain.Specification.allOf;
import static ro.george.postelnicu.geolibrary.specification.CollectionFieldSpecification.buildCollectionsSpecification;
import static ro.george.postelnicu.geolibrary.specification.NumericalFieldSpecification.buildNumericalSpecification;
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
        specifications.add(buildNameFullTitleAndDescription(searchCriteria));
        specifications.add(buildIsbnAndBarcode(searchCriteria));
        specifications.add(buildPublisherAndCover(searchCriteria));
        specifications.add(buildPublishYear(searchCriteria));
        specifications.add(buildPages(searchCriteria));
        specifications.add(buildAuthors(searchCriteria));
        specifications.add(buildKeywords(searchCriteria));
        specifications.add(buildLanguages(searchCriteria));

        return repository.findAll(allOf(specifications), pageRequest);
    }

    private static Specification<Book> buildNameFullTitleAndDescription(BookSearchCriteria searchCriteria) {
        Specification<Book> name = buildSpecification(searchCriteria.name(), root -> root.get("name"));
        Specification<Book> fullTitle = buildSpecification(searchCriteria.fullTitle(), root -> root.get("fullTitle"));
        Specification<Book> description = buildSpecification(searchCriteria.description(), root -> root.get("description"));

        return allOf(name, fullTitle, description);
    }

    private Specification<Book> buildIsbnAndBarcode(BookSearchCriteria searchCriteria) {
        Specification<Book> isbn = buildSpecification(searchCriteria.isbn(), root -> root.get("isbn"));
        Specification<Book> fullTitle = buildSpecification(searchCriteria.barcode(), root -> root.get("barcode"));

        return allOf(isbn, fullTitle);
    }

    private static Specification<Book> buildPublisherAndCover(BookSearchCriteria searchCriteria) {
        Specification<Book> publisherSpec = buildSpecification(searchCriteria.publisher(), root -> root.get("publisher"));
        Specification<Book> coverTypeSpec = buildSpecification(searchCriteria.coverType(), root -> root.get("cover"));

        return allOf(publisherSpec, coverTypeSpec);
    }

    private static Specification<Book> buildPublishYear(BookSearchCriteria searchCriteria) {
        return buildNumericalSpecification(searchCriteria.minYear(), searchCriteria.maxYear(),
                root -> root.get("publishYear"));
    }

    private static Specification<Book> buildPages(BookSearchCriteria searchCriteria) {
        return buildNumericalSpecification(searchCriteria.minPages(), searchCriteria.maxPages(),
                root -> root.get("pages"));
    }

    private static Specification<Book> buildAuthors(BookSearchCriteria searchCriteria) {
        return buildCollectionsSpecification(searchCriteria.authors(), root -> root.join("authors").get("name"));
    }

    private static Specification<Book> buildKeywords(BookSearchCriteria searchCriteria) {
        return buildCollectionsSpecification(searchCriteria.keywords(), root -> root.join("keywords").get("name"));
    }

    private static Specification<Book> buildLanguages(BookSearchCriteria searchCriteria) {
        return buildCollectionsSpecification(searchCriteria.languages(), root -> root.join("languages").get("name"));
    }

}
