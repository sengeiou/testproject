package com.hepaestus.testproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hepaestus.testproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dog.class);
        Dog dog1 = new Dog();
        dog1.setId(1L);
        Dog dog2 = new Dog();
        dog2.setId(dog1.getId());
        assertThat(dog1).isEqualTo(dog2);
        dog2.setId(2L);
        assertThat(dog1).isNotEqualTo(dog2);
        dog1.setId(null);
        assertThat(dog1).isNotEqualTo(dog2);
    }
}
