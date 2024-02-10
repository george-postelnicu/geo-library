package ro.george.postelnicu.geolibrary.exception;

public class EntityAlreadyLinkedException extends RuntimeException {

    public static final String ENTITY_ALREADY_HAS_A_LINK = "Entity [%s] already has a link to [%s]";

    public EntityAlreadyLinkedException(String entityName, String duplicate) {
        super(String.format(ENTITY_ALREADY_HAS_A_LINK, entityName, duplicate));
    }

}
