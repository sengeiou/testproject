package com.hepaestus.testproject.service.dto;

import com.hepaestus.testproject.domain.enumeration.AddressType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.hepaestus.testproject.domain.Actor} entity.
 */
public class ActorDTO implements Serializable {

    private Long id;

    private String name;

    private String firstName;

    private String lastName;

    private AddressType type;

    private BookDTO book;

    private Set<EchoDTO> echoes = new HashSet<>();

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    public Set<EchoDTO> getEchoes() {
        return echoes;
    }

    public void setEchoes(Set<EchoDTO> echoes) {
        this.echoes = echoes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActorDTO)) {
            return false;
        }

        ActorDTO actorDTO = (ActorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, actorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActorDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", type='" + getType() + "'" +
            ", book=" + getBook() +
            ", echoes=" + getEchoes() +
            "}";
    }
}
