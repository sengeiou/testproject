package com.hepaestus.testproject.repository.search;

import com.hepaestus.testproject.domain.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Book} entity.
 */
public interface BookSearchRepository extends ElasticsearchRepository<Book, Long> {}
