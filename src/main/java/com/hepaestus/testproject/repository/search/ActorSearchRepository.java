package com.hepaestus.testproject.repository.search;

import com.hepaestus.testproject.domain.Actor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Actor} entity.
 */
public interface ActorSearchRepository extends ElasticsearchRepository<Actor, Long> {}
