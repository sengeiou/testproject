package com.hepaestus.testproject.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hepaestus.testproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ActorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActorDTO.class);
        ActorDTO actorDTO1 = new ActorDTO();
        actorDTO1.setId(1L);
        ActorDTO actorDTO2 = new ActorDTO();
        assertThat(actorDTO1).isNotEqualTo(actorDTO2);
        actorDTO2.setId(actorDTO1.getId());
        assertThat(actorDTO1).isEqualTo(actorDTO2);
        actorDTO2.setId(2L);
        assertThat(actorDTO1).isNotEqualTo(actorDTO2);
        actorDTO1.setId(null);
        assertThat(actorDTO1).isNotEqualTo(actorDTO2);
    }
}
