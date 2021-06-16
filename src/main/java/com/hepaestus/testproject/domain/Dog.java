package com.hepaestus.testproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A Dog.
 */
@Entity
@Table(name = "dog")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "dog")
public class Dog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDate created;

    @OneToMany(mappedBy = "dog")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "dog" }, allowSetters = true)
    private Set<Car> cards = new HashSet<>();

    @JsonIgnoreProperties(value = { "dog", "books", "echoes" }, allowSetters = true)
    @OneToOne(mappedBy = "dog")
    private Author author;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dog id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Dog name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Dog description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreated() {
        return this.created;
    }

    public Dog created(LocalDate created) {
        this.created = created;
        return this;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public Set<Car> getCards() {
        return this.cards;
    }

    public Dog cards(Set<Car> cars) {
        this.setCards(cars);
        return this;
    }

    public Dog addCard(Car car) {
        this.cards.add(car);
        car.setDog(this);
        return this;
    }

    public Dog removeCard(Car car) {
        this.cards.remove(car);
        car.setDog(null);
        return this;
    }

    public void setCards(Set<Car> cars) {
        if (this.cards != null) {
            this.cards.forEach(i -> i.setDog(null));
        }
        if (cars != null) {
            cars.forEach(i -> i.setDog(this));
        }
        this.cards = cars;
    }

    public Author getAuthor() {
        return this.author;
    }

    public Dog author(Author author) {
        this.setAuthor(author);
        return this;
    }

    public void setAuthor(Author author) {
        if (this.author != null) {
            this.author.setDog(null);
        }
        if (author != null) {
            author.setDog(this);
        }
        this.author = author;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dog)) {
            return false;
        }
        return id != null && id.equals(((Dog) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dog{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", created='" + getCreated() + "'" +
            "}";
    }
}
