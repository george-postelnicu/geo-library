package ro.george.postelnicu.geolibrary.repository;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ro.george.postelnicu.geolibrary.model.Book;
import ro.george.postelnicu.geolibrary.model.Keyword;
import ro.george.postelnicu.geolibrary.model.Language;

@Repository
public class DefaultDatabaseRepository {
    private final EntityManager entityManager;

    @Autowired
    public DefaultDatabaseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void initDb() {
        Keyword art = new Keyword("Art");
        Keyword architecture = new Keyword("Architecture");
        Keyword estonia = new Keyword("Estonia");
        Keyword kumuMuseum = new Keyword("Kumu Museum");
        Language english = new Language("English");
        Language estonian = new Language("Estonian");
        Book landscapesOfIdentity = new Book("Landscapes of Identity", "ISBN 978-9949-687-32-9");
        Book conflictsAndAdaptations = new Book("Conflicts and Adaptations", "ISBN 978-9949-687-44-2");
        Book oneHundredStepsEstonian = new Book("100 Steps Through 20th Century Estonian Architecture", "ISBN 978-9949-9078-6-1");

        landscapesOfIdentity.addKeyword(art);
        landscapesOfIdentity.addKeyword(estonia);
        landscapesOfIdentity.addKeyword(kumuMuseum);
        landscapesOfIdentity.addLanguage(english);

        conflictsAndAdaptations.addKeyword(art);
        conflictsAndAdaptations.addKeyword(estonia);
        conflictsAndAdaptations.addKeyword(kumuMuseum);

        oneHundredStepsEstonian.addKeyword(architecture);
        oneHundredStepsEstonian.addKeyword(estonia);
        oneHundredStepsEstonian.addLanguage(estonian);
        oneHundredStepsEstonian.addLanguage(english);

        entityManager.persist(landscapesOfIdentity);
        entityManager.persist(conflictsAndAdaptations);
        entityManager.persist(oneHundredStepsEstonian);
    }
}
