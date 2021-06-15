package com.hepaestus.testproject.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hepaestus.testproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DogDTO.class);
        DogDTO dogDTO1 = new DogDTO();
        dogDTO1.setId(1L);
        DogDTO dogDTO2 = new DogDTO();
        assertThat(dogDTO1).isNotEqualTo(dogDTO2);
        dogDTO2.setId(dogDTO1.getId());
        assertThat(dogDTO1).isEqualTo(dogDTO2);
        dogDTO2.setId(2L);
        assertThat(dogDTO1).isNotEqualTo(dogDTO2);
        dogDTO1.setId(null);
        assertThat(dogDTO1).isNotEqualTo(dogDTO2);
    }
}
