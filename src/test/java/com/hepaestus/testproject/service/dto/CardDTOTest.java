package com.hepaestus.testproject.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hepaestus.testproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CardDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CardDTO.class);
        CardDTO cardDTO1 = new CardDTO();
        cardDTO1.setId(1L);
        CardDTO cardDTO2 = new CardDTO();
        assertThat(cardDTO1).isNotEqualTo(cardDTO2);
        cardDTO2.setId(cardDTO1.getId());
        assertThat(cardDTO1).isEqualTo(cardDTO2);
        cardDTO2.setId(2L);
        assertThat(cardDTO1).isNotEqualTo(cardDTO2);
        cardDTO1.setId(null);
        assertThat(cardDTO1).isNotEqualTo(cardDTO2);
    }
}
