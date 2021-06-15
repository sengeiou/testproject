package com.hepaestus.testproject.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testproject.domain.Actor;
import com.hepaestus.testproject.repository.ActorRepository;
import com.hepaestus.testproject.repository.search.ActorSearchRepository;
import com.hepaestus.testproject.service.dto.ActorDTO;
import com.hepaestus.testproject.service.mapper.ActorMapper;
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
 * Service Implementation for managing {@link Actor}.
 */
@Service
@Transactional
public class ActorService {

    private final Logger log = LoggerFactory.getLogger(ActorService.class);

    private final ActorRepository actorRepository;

    private final ActorMapper actorMapper;

    private final ActorSearchRepository actorSearchRepository;

    public ActorService(ActorRepository actorRepository, ActorMapper actorMapper, ActorSearchRepository actorSearchRepository) {
        this.actorRepository = actorRepository;
        this.actorMapper = actorMapper;
        this.actorSearchRepository = actorSearchRepository;
    }

    /**
     * Save a actor.
     *
     * @param actorDTO the entity to save.
     * @return the persisted entity.
     */
    public ActorDTO save(ActorDTO actorDTO) {
        log.debug("Request to save Actor : {}", actorDTO);
        Actor actor = actorMapper.toEntity(actorDTO);
        actor = actorRepository.save(actor);
        ActorDTO result = actorMapper.toDto(actor);
        actorSearchRepository.save(actor);
        return result;
    }

    /**
     * Partially update a actor.
     *
     * @param actorDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ActorDTO> partialUpdate(ActorDTO actorDTO) {
        log.debug("Request to partially update Actor : {}", actorDTO);

        return actorRepository
            .findById(actorDTO.getId())
            .map(
                existingActor -> {
                    actorMapper.partialUpdate(existingActor, actorDTO);
                    return existingActor;
                }
            )
            .map(actorRepository::save)
            .map(
                savedActor -> {
                    actorSearchRepository.save(savedActor);

                    return savedActor;
                }
            )
            .map(actorMapper::toDto);
    }

    /**
     * Get all the actors.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ActorDTO> findAll() {
        log.debug("Request to get all Actors");
        return actorRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(actorMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the actors with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ActorDTO> findAllWithEagerRelationships(Pageable pageable) {
        return actorRepository.findAllWithEagerRelationships(pageable).map(actorMapper::toDto);
    }

    /**
     * Get one actor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ActorDTO> findOne(Long id) {
        log.debug("Request to get Actor : {}", id);
        return actorRepository.findOneWithEagerRelationships(id).map(actorMapper::toDto);
    }

    /**
     * Delete the actor by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Actor : {}", id);
        actorRepository.deleteById(id);
        actorSearchRepository.deleteById(id);
    }

    /**
     * Search for the actor corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ActorDTO> search(String query) {
        log.debug("Request to search Actors for query {}", query);
        return StreamSupport
            .stream(actorSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(actorMapper::toDto)
            .collect(Collectors.toList());
    }
}
