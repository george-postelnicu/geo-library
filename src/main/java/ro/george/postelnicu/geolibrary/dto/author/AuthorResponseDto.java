package ro.george.postelnicu.geolibrary.dto.author;

import jakarta.validation.constraints.NotNull;

public class AuthorResponseDto extends AuthorDto {
    @NotNull
    private Long id;

    public AuthorResponseDto(String name, Long id) {
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
