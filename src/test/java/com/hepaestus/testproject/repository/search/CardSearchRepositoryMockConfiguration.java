package com.hepaestus.testproject.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link CardSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CardSearchRepositoryMockConfiguration {

    @MockBean
    private CardSearchRepository mockCardSearchRepository;
}
