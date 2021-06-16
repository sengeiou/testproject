package com.hepaestus.testproject.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testproject.domain.Author;
import com.hepaestus.testproject.repository.AuthorRepository;
import com.hepaestus.testproject.repository.search.AuthorSearchRepository;
import com.hepaestus.testproject.service.dto.AuthorDTO;
import com.hepaestus.testproject.service.mapper.AuthorMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Author}.
 */
@Service
@Transactional
public class AuthorService {

    private final Logger log = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    private final AuthorSearchRepository authorSearchRepository;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper, AuthorSearchRepository authorSearchRepository) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.authorSearchRepository = authorSearchRepository;
    }

    /**
     * Save a author.
     *
     * @param authorDTO the entity to save.
     * @return the persisted entity.
     */
    public AuthorDTO save(AuthorDTO authorDTO) {
        log.debug("Request to save Author : {}", authorDTO);
        Author author = authorMapper.toEntity(authorDTO);
        author = authorRepository.save(author);
        AuthorDTO result = authorMapper.toDto(author);
        authorSearchRepository.save(author);
        return result;
    }

    /**
     * Partially update a author.
     *
     * @param authorDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AuthorDTO> partialUpdate(AuthorDTO authorDTO) {
        log.debug("Request to partially update Author : {}", authorDTO);

        return authorRepository
            .findById(authorDTO.getId())
            .map(
                existingAuthor -> {
                    authorMapper.partialUpdate(existingAuthor, authorDTO);
                    return existingAuthor;
                }
            )
            .map(authorRepository::save)
            .map(
                savedAuthor -> {
                    authorSearchRepository.save(savedAuthor);

                    return savedAuthor;
                }
            )
            .map(authorMapper::toDto);
    }

    /**
     * Get all the authors.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AuthorDTO> findAll() {
        log.debug("Request to get all Authors");
        return authorRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(authorMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the authors with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AuthorDTO> findAllWithEagerRelationships(Pageable pageable) {
        return authorRepository.findAllWithEagerRelationships(pageable).map(authorMapper::toDto);
    }

    /**
     * Get one author by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AuthorDTO> findOne(Long id) {
        log.debug("Request to get Author : {}", id);
        return authorRepository.findOneWithEagerRelationships(id).map(authorMapper::toDto);
    }

    /**
     * Delete the author by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Author : {}", id);
        authorRepository.deleteById(id);
        authorSearchRepository.deleteById(id);
    }

    /**
     * Search for the author corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AuthorDTO> search(String query) {
        log.debug("Request to search Authors for query {}", query);
        return StreamSupport
            .stream(authorSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(authorMapper::toDto)
            .collect(Collectors.toList());
    }
}
