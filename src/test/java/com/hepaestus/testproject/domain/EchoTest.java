package com.hepaestus.testproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hepaestus.testproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EchoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Echo.class);
        Echo echo1 = new Echo();
        echo1.setId(1L);
        Echo echo2 = new Echo();
        echo2.setId(echo1.getId());
        assertThat(echo1).isEqualTo(echo2);
        echo2.setId(2L);
        assertThat(echo1).isNotEqualTo(echo2);
        echo1.setId(null);
        assertThat(echo1).isNotEqualTo(echo2);
    }
}
