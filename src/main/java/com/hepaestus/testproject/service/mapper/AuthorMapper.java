package com.hepaestus.testproject.service.mapper;

import com.hepaestus.testproject.domain.*;
import com.hepaestus.testproject.service.dto.AuthorDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Author} and its DTO {@link AuthorDTO}.
 */
@Mapper(componentModel = "spring", uses = { DogMapper.class, EchoMapper.class })
public interface AuthorMapper extends EntityMapper<AuthorDTO, Author> {
    @Mapping(target = "dog", source = "dog", qualifiedByName = "name")
    @Mapping(target = "echoes", source = "echoes", qualifiedByName = "idSet")
    AuthorDTO toDto(Author s);

    @Mapping(target = "removeEcho", ignore = true)
    Author toEntity(AuthorDTO authorDTO);

    @Named("name")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AuthorDTO toDtoName(Author author);
}
