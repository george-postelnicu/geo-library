package ro.george.postelnicu.geolibrary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ro.george.postelnicu.geolibrary.util.PageModule;

@TestConfiguration
public class ObjectMapperConfiguration {

  @Bean
  public ObjectMapper setUp() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
//    objectMapper.setAnnotationIntrospector(new EmptyArrayJacksonAnnotationIntrospector());
    objectMapper.registerModule(new PageModule());
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
    return objectMapper;
  }

}
