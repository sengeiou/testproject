package com.hepaestus.testproject.repository.search;

import com.hepaestus.testproject.domain.Echo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Echo} entity.
 */
public interface EchoSearchRepository extends ElasticsearchRepository<Echo, Long> {}
