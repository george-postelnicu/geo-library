package ro.george.postelnicu.geolibrary.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.util.Set;

public class ErrorDto {
    private String traceId;
    private String title;
    private String detail;
    private Set<String> additionalInformation;
    private HttpStatus status;

    @JsonCreator
    public ErrorDto(@JsonProperty("traceId") String traceId,
                    @JsonProperty("title") String title,
                    @JsonProperty("detail") String detail,
                    @JsonProperty("additionalInformation") Set<String> additionalInformation,
                    @JsonProperty("status") HttpStatus status) {
        this.traceId = traceId;
        this.title = title;
        this.detail = detail;
        this.additionalInformation = additionalInformation;
        this.status = status;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Set<String> getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(Set<String> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}