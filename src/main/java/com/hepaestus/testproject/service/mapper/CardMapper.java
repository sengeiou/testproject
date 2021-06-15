package com.hepaestus.testproject.service.mapper;

import com.hepaestus.testproject.domain.*;
import com.hepaestus.testproject.service.dto.CardDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Card} and its DTO {@link CardDTO}.
 */
@Mapper(componentModel = "spring", uses = { DogMapper.class })
public interface CardMapper extends EntityMapper<CardDTO, Card> {
    @Mapping(target = "dog", source = "dog", qualifiedByName = "id")
    CardDTO toDto(Card s);
}
