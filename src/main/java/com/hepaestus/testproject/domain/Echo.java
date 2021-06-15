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
 * A Echo.
 */
@Entity
@Table(name = "echo")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "echo")
public class Echo implements Serializable {

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

    @ManyToMany(mappedBy = "echoes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "book", "dogs", "echoes" }, allowSetters = true)
    private Set<Actor> actors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Echo id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Echo name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Echo description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreated() {
        return this.created;
    }

    public Echo created(LocalDate created) {
        this.created = created;
        return this;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public Set<Actor> getActors() {
        return this.actors;
    }

    public Echo actors(Set<Actor> actors) {
        this.setActors(actors);
        return this;
    }

    public Echo addActor(Actor actor) {
        this.actors.add(actor);
        actor.getEchoes().add(this);
        return this;
    }

    public Echo removeActor(Actor actor) {
        this.actors.remove(actor);
        actor.getEchoes().remove(this);
        return this;
    }

    public void setActors(Set<Actor> actors) {
        if (this.actors != null) {
            this.actors.forEach(i -> i.removeEcho(this));
        }
        if (actors != null) {
            actors.forEach(i -> i.addEcho(this));
        }
        this.actors = actors;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Echo)) {
            return false;
        }
        return id != null && id.equals(((Echo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Echo{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", created='" + getCreated() + "'" +
            "}";
    }
}
