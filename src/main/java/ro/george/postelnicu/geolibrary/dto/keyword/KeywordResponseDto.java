package ro.george.postelnicu.geolibrary.dto.keyword;

import jakarta.validation.constraints.NotNull;

public class KeywordResponseDto extends KeywordDto {
    @NotNull
    private Long id;

    public KeywordResponseDto(String name, Long id) {
        super(name);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
