package com.hepaestus.testproject.repository.search;

import com.hepaestus.testproject.domain.Car;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Car} entity.
 */
public interface CarSearchRepository extends ElasticsearchRepository<Car, Long> {}
