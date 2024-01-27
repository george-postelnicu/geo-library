package ro.george.postelnicu.geolibrary.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.BookSearchCriteria;
import ro.george.postelnicu.geolibrary.repository.BookSpecificationRepository;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
public class BookSearchService {

    private final BookSpecificationRepository repository;

    @Autowired
    public BookSearchService(BookSpecificationRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = REQUIRED, readOnly = true)
    public Page<Book> search(@NotNull @Valid BookSearchCriteria searchCriteria, @NotNull Pageable pageRequest) {
        return repository.search(searchCriteria, pageRequest);
    }

}
