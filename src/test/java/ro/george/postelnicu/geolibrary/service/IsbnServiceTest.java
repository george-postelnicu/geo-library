package ro.george.postelnicu.geolibrary.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ro.george.postelnicu.geolibrary.AbstractIntegrationTest;
import ro.george.postelnicu.geolibrary.exception.EntityValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ro.george.postelnicu.geolibrary.exception.EntityValidationException.ENTITY_VALIDATION_FAILURE;
import static ro.george.postelnicu.geolibrary.model.EntityName.BOOK;

class IsbnServiceTest extends AbstractIntegrationTest {

    public static final String INVALID_ISBN = "Invalid ISBN";
    public static final String INVALID_ISBN_CHECK_DIGIT = "Invalid ISBN check digit";
    private final IsbnService service;

    @Autowired
    IsbnServiceTest(IsbnService service) {
        this.service = service;
    }

    @Test
    void isbnCheck_isSuccessful_whenAllPositiveScenariosForIsbnAreGiven() {
        service.isValid("ISBN 978-0-596-52068-7");
        service.isValid("ISBN-13: 978-0-596-52068-7");
        service.isValid("978 0 596 52068 7");
        service.isValid("9780596520687");
        service.isValid("ISBN-10 0-596-52068-9");
        service.isValid("0-596-52068-9");
        service.isValid("ISBN 9971-5-0210-0");
        service.isValid("0-8044-2957-X");
    }

    @Test
    void isbnCheck_throwsException_whenIsbnGroupIsNot_978_979() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> service.isValid("ISBN 777-0-596-52068-7"));

        assertEquals(ex.getMessage(), String.format(ENTITY_VALIDATION_FAILURE, BOOK, INVALID_ISBN));
    }

    @Test
    void isbnCheck_throwsException_whenIsbnHasTypo() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> service.isValid("IBSN 978-0-596-52068-7"));

        assertEquals(ex.getMessage(), String.format(ENTITY_VALIDATION_FAILURE, BOOK, INVALID_ISBN));
    }

    @Test
    void isbnCheck_throwsException_whenIsbn13CheckSumFails() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> service.isValid("ISBN 978-0-596-52068-8"));

        assertEquals(ex.getMessage(), String.format(ENTITY_VALIDATION_FAILURE, BOOK, INVALID_ISBN_CHECK_DIGIT));
    }

    @Test
    void isbnCheck_throwsException_whenIsbn10CheckSumFails() {
        EntityValidationException ex = assertThrows(EntityValidationException.class,
                () -> service.isValid("ISBN-10 0-596-52068-8"));

        assertEquals(ex.getMessage(), String.format(ENTITY_VALIDATION_FAILURE, BOOK, INVALID_ISBN_CHECK_DIGIT));
    }
}