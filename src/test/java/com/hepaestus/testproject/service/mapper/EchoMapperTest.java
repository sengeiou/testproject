package com.hepaestus.testproject.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EchoMapperTest {

    private EchoMapper echoMapper;

    @BeforeEach
    public void setUp() {
        echoMapper = new EchoMapperImpl();
    }
}
