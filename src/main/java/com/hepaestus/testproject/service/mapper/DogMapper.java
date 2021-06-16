package com.hepaestus.testproject.service.mapper;

import com.hepaestus.testproject.domain.*;
import com.hepaestus.testproject.service.dto.DogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Dog} and its DTO {@link DogDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DogMapper extends EntityMapper<DogDTO, Dog> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DogDTO toDtoId(Dog dog);

    @Named("name")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DogDTO toDtoName(Dog dog);
}
