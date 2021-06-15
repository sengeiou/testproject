package com.hepaestus.testproject.service.mapper;

import com.hepaestus.testproject.domain.*;
import com.hepaestus.testproject.service.dto.DogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Dog} and its DTO {@link DogDTO}.
 */
@Mapper(componentModel = "spring", uses = { ActorMapper.class })
public interface DogMapper extends EntityMapper<DogDTO, Dog> {
    @Mapping(target = "actor", source = "actor", qualifiedByName = "name")
    DogDTO toDto(Dog s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DogDTO toDtoId(Dog dog);
}
