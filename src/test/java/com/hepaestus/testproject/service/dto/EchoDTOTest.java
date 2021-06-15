package com.hepaestus.testproject.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hepaestus.testproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EchoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EchoDTO.class);
        EchoDTO echoDTO1 = new EchoDTO();
        echoDTO1.setId(1L);
        EchoDTO echoDTO2 = new EchoDTO();
        assertThat(echoDTO1).isNotEqualTo(echoDTO2);
        echoDTO2.setId(echoDTO1.getId());
        assertThat(echoDTO1).isEqualTo(echoDTO2);
        echoDTO2.setId(2L);
        assertThat(echoDTO1).isNotEqualTo(echoDTO2);
        echoDTO1.setId(null);
        assertThat(echoDTO1).isNotEqualTo(echoDTO2);
    }
}
