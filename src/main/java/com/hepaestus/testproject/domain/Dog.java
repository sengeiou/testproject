package com.hepaestus.testproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
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

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created")
    private LocalDate created;

    @OneToMany(mappedBy = "dog")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "dog" }, allowSetters = true)
    private Set<Card> cards = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "book", "dogs", "echoes" }, allowSetters = true)
    private Actor actor;

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

    public Set<Card> getCards() {
        return this.cards;
    }

    public Dog cards(Set<Card> cards) {
        this.setCards(cards);
        return this;
    }

    public Dog addCard(Card card) {
        this.cards.add(card);
        card.setDog(this);
        return this;
    }

    public Dog removeCard(Card card) {
        this.cards.remove(card);
        card.setDog(null);
        return this;
    }

    public void setCards(Set<Card> cards) {
        if (this.cards != null) {
            this.cards.forEach(i -> i.setDog(null));
        }
        if (cards != null) {
            cards.forEach(i -> i.setDog(this));
        }
        this.cards = cards;
    }

    public Actor getActor() {
        return this.actor;
    }

    public Dog actor(Actor actor) {
        this.setActor(actor);
        return this;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
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
