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
    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<EchoDTO> toDtoIdSet(Set<Echo> echo);
}
