package ro.george.postelnicu.geolibrary.model;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "keyword")
public class Keyword {

    @Id
    @GeneratedValue
    private Long id;
    @NaturalId(mutable = true)
    @Column(unique = true)
    private String name;
    @ManyToMany(mappedBy = "keywords")
    private Set<Book> books = new HashSet<>();

    public Keyword() {
    }

    public Keyword(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keyword keyword = (Keyword) o;
        return Objects.equals(name, keyword.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
