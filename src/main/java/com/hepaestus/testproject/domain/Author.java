package com.hepaestus.testproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hepaestus.testproject.domain.enumeration.AddressType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Author.
 */
@Entity
@Table(name = "author")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "author")
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AddressType type;

    @JsonIgnoreProperties(value = { "cards", "author" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Dog dog;

    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "author" }, allowSetters = true)
    private Set<Book> books = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "rel_author__echo", joinColumns = @JoinColumn(name = "author_id"), inverseJoinColumns = @JoinColumn(name = "echo_id"))
    @JsonIgnoreProperties(value = { "authors" }, allowSetters = true)
    private Set<Echo> echoes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Author id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Author name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Author firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Author lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AddressType getType() {
        return this.type;
    }

    public Author type(AddressType type) {
        this.type = type;
        return this;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public Dog getDog() {
        return this.dog;
    }

    public Author dog(Dog dog) {
        this.setDog(dog);
        return this;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }

    public Set<Book> getBooks() {
        return this.books;
    }

    public Author books(Set<Book> books) {
        this.setBooks(books);
        return this;
    }

    public Author addBook(Book book) {
        this.books.add(book);
        book.setAuthor(this);
        return this;
    }

    public Author removeBook(Book book) {
        this.books.remove(book);
        book.setAuthor(null);
        return this;
    }

    public void setBooks(Set<Book> books) {
        if (this.books != null) {
            this.books.forEach(i -> i.setAuthor(null));
        }
        if (books != null) {
            books.forEach(i -> i.setAuthor(this));
        }
        this.books = books;
    }

    public Set<Echo> getEchoes() {
        return this.echoes;
    }

    public Author echoes(Set<Echo> echoes) {
        this.setEchoes(echoes);
        return this;
    }

    public Author addEcho(Echo echo) {
        this.echoes.add(echo);
        echo.getAuthors().add(this);
        return this;
    }

    public Author removeEcho(Echo echo) {
        this.echoes.remove(echo);
        echo.getAuthors().remove(this);
        return this;
    }

    public void setEchoes(Set<Echo> echoes) {
        this.echoes = echoes;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Author)) {
            return false;
        }
        return id != null && id.equals(((Author) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Author{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
