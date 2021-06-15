package com.hepaestus.testproject.service.mapper;

import com.hepaestus.testproject.domain.*;
import com.hepaestus.testproject.service.dto.ActorDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Actor} and its DTO {@link ActorDTO}.
 */
@Mapper(componentModel = "spring", uses = { BookMapper.class, EchoMapper.class })
public interface ActorMapper extends EntityMapper<ActorDTO, Actor> {
    @Mapping(target = "book", source = "book", qualifiedByName = "id")
    @Mapping(target = "echoes", source = "echoes", qualifiedByName = "nameSet")
    ActorDTO toDto(Actor s);

    @Mapping(target = "removeEcho", ignore = true)
    Actor toEntity(ActorDTO actorDTO);

    @Named("name")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ActorDTO toDtoName(Actor actor);
}
