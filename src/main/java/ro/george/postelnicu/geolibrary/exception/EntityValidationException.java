package ro.george.postelnicu.geolibrary.exception;

public class EntityValidationException extends RuntimeException {
    public static final String ENTITY_VALIDATION_FAILURE = "Entity [%s] has this validation failure [%s]";

    public EntityValidationException(String entityName, String duplicate) {
        super(String.format(ENTITY_VALIDATION_FAILURE, entityName, duplicate));
    }
}
