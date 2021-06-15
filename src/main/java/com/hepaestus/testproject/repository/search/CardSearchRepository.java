package com.hepaestus.testproject.repository.search;

import com.hepaestus.testproject.domain.Card;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Card} entity.
 */
public interface CardSearchRepository extends ElasticsearchRepository<Card, Long> {}
