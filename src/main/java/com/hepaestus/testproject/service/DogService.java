package com.hepaestus.testproject.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testproject.domain.Dog;
import com.hepaestus.testproject.repository.DogRepository;
import com.hepaestus.testproject.repository.search.DogSearchRepository;
import com.hepaestus.testproject.service.dto.DogDTO;
import com.hepaestus.testproject.service.mapper.DogMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Dog}.
 */
@Service
@Transactional
public class DogService {

    private final Logger log = LoggerFactory.getLogger(DogService.class);

    private final DogRepository dogRepository;

    private final DogMapper dogMapper;

    private final DogSearchRepository dogSearchRepository;

    public DogService(DogRepository dogRepository, DogMapper dogMapper, DogSearchRepository dogSearchRepository) {
        this.dogRepository = dogRepository;
        this.dogMapper = dogMapper;
        this.dogSearchRepository = dogSearchRepository;
    }

    /**
     * Save a dog.
     *
     * @param dogDTO the entity to save.
     * @return the persisted entity.
     */
    public DogDTO save(DogDTO dogDTO) {
        log.debug("Request to save Dog : {}", dogDTO);
        Dog dog = dogMapper.toEntity(dogDTO);
        dog = dogRepository.save(dog);
        DogDTO result = dogMapper.toDto(dog);
        dogSearchRepository.save(dog);
        return result;
    }

    /**
     * Partially update a dog.
     *
     * @param dogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DogDTO> partialUpdate(DogDTO dogDTO) {
        log.debug("Request to partially update Dog : {}", dogDTO);

        return dogRepository
            .findById(dogDTO.getId())
            .map(
                existingDog -> {
                    dogMapper.partialUpdate(existingDog, dogDTO);
                    return existingDog;
                }
            )
            .map(dogRepository::save)
            .map(
                savedDog -> {
                    dogSearchRepository.save(savedDog);

                    return savedDog;
                }
            )
            .map(dogMapper::toDto);
    }

    /**
     * Get all the dogs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<DogDTO> findAll() {
        log.debug("Request to get all Dogs");
        return dogRepository.findAll().stream().map(dogMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one dog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DogDTO> findOne(Long id) {
        log.debug("Request to get Dog : {}", id);
        return dogRepository.findById(id).map(dogMapper::toDto);
    }

    /**
     * Delete the dog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Dog : {}", id);
        dogRepository.deleteById(id);
        dogSearchRepository.deleteById(id);
    }

    /**
     * Search for the dog corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<DogDTO> search(String query) {
        log.debug("Request to search Dogs for query {}", query);
        return StreamSupport
            .stream(dogSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(dogMapper::toDto)
            .collect(Collectors.toList());
    }
}
