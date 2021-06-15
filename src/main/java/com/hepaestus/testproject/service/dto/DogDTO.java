package com.hepaestus.testproject.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.hepaestus.testproject.domain.Dog} entity.
 */
public class DogDTO implements Serializable {

    private Long id;

    private String name;

    private String description;

    private LocalDate created;

    private ActorDTO actor;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public ActorDTO getActor() {
        return actor;
    }

    public void setActor(ActorDTO actor) {
        this.actor = actor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DogDTO)) {
            return false;
        }

        DogDTO dogDTO = (DogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DogDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", created='" + getCreated() + "'" +
            ", actor=" + getActor() +
            "}";
    }
}
