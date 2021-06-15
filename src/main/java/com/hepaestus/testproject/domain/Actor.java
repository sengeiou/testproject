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
 * A Actor.
 */
@Entity
@Table(name = "actor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "actor")
public class Actor implements Serializable {

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

    @JsonIgnoreProperties(value = { "actor" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Book book;

    @OneToMany(mappedBy = "actor")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cards", "actor" }, allowSetters = true)
    private Set<Dog> dogs = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "rel_actor__echo", joinColumns = @JoinColumn(name = "actor_id"), inverseJoinColumns = @JoinColumn(name = "echo_id"))
    @JsonIgnoreProperties(value = { "actors" }, allowSetters = true)
    private Set<Echo> echoes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Actor id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Actor name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Actor firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Actor lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AddressType getType() {
        return this.type;
    }

    public Actor type(AddressType type) {
        this.type = type;
        return this;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public Book getBook() {
        return this.book;
    }

    public Actor book(Book book) {
        this.setBook(book);
        return this;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Set<Dog> getDogs() {
        return this.dogs;
    }

    public Actor dogs(Set<Dog> dogs) {
        this.setDogs(dogs);
        return this;
    }

    public Actor addDog(Dog dog) {
        this.dogs.add(dog);
        dog.setActor(this);
        return this;
    }

    public Actor removeDog(Dog dog) {
        this.dogs.remove(dog);
        dog.setActor(null);
        return this;
    }

    public void setDogs(Set<Dog> dogs) {
        if (this.dogs != null) {
            this.dogs.forEach(i -> i.setActor(null));
        }
        if (dogs != null) {
            dogs.forEach(i -> i.setActor(this));
        }
        this.dogs = dogs;
    }

    public Set<Echo> getEchoes() {
        return this.echoes;
    }

    public Actor echoes(Set<Echo> echoes) {
        this.setEchoes(echoes);
        return this;
    }

    public Actor addEcho(Echo echo) {
        this.echoes.add(echo);
        echo.getActors().add(this);
        return this;
    }

    public Actor removeEcho(Echo echo) {
        this.echoes.remove(echo);
        echo.getActors().remove(this);
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
        if (!(o instanceof Actor)) {
            return false;
        }
        return id != null && id.equals(((Actor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Actor{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
