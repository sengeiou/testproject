package com.hepaestus.testproject.service.mapper;

import com.hepaestus.testproject.domain.*;
import com.hepaestus.testproject.service.dto.CarDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Car} and its DTO {@link CarDTO}.
 */
@Mapper(componentModel = "spring", uses = { DogMapper.class })
public interface CarMapper extends EntityMapper<CarDTO, Car> {
    @Mapping(target = "dog", source = "dog", qualifiedByName = "id")
    CarDTO toDto(Car s);
}
