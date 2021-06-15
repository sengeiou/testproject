package com.hepaestus.testproject.repository.search;

import com.hepaestus.testproject.domain.Dog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Dog} entity.
 */
public interface DogSearchRepository extends ElasticsearchRepository<Dog, Long> {}
