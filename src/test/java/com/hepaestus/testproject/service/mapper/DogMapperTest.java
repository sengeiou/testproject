package com.hepaestus.testproject.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DogMapperTest {

    private DogMapper dogMapper;

    @BeforeEach
    public void setUp() {
        dogMapper = new DogMapperImpl();
    }
}
