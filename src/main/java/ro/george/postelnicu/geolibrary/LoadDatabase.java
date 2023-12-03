package ro.george.postelnicu.geolibrary;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.Keyword;
import ro.george.postelnicu.geolibrary.model.Language;
import ro.george.postelnicu.geolibrary.repository.BookRepository;
import ro.george.postelnicu.geolibrary.repository.DefaultDatabaseRepository;
import ro.george.postelnicu.geolibrary.repository.KeywordRepository;

import java.util.Set;

//@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

//    @Bean
    CommandLineRunner initDatabase(DefaultDatabaseRepository repository) {
        return args -> {
            log.info("Preloading data base");
            repository.initDb();
        };
    }
}
