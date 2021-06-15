package com.hepaestus.testproject.service.mapper;

import com.hepaestus.testproject.domain.*;
import com.hepaestus.testproject.service.dto.EchoDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Echo} and its DTO {@link EchoDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EchoMapper extends EntityMapper<EchoDTO, Echo> {
    @Named("nameSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Set<EchoDTO> toDtoNameSet(Set<Echo> echo);
}
