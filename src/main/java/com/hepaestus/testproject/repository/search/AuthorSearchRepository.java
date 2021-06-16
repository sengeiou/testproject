package com.hepaestus.testproject.repository.search;

import com.hepaestus.testproject.domain.Author;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Author} entity.
 */
public interface AuthorSearchRepository extends ElasticsearchRepository<Author, Long> {}
