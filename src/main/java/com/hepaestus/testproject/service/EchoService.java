package com.hepaestus.testproject.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testproject.domain.Echo;
import com.hepaestus.testproject.repository.EchoRepository;
import com.hepaestus.testproject.repository.search.EchoSearchRepository;
import com.hepaestus.testproject.service.dto.EchoDTO;
import com.hepaestus.testproject.service.mapper.EchoMapper;
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
 * Service Implementation for managing {@link Echo}.
 */
@Service
@Transactional
public class EchoService {

    private final Logger log = LoggerFactory.getLogger(EchoService.class);

    private final EchoRepository echoRepository;

    private final EchoMapper echoMapper;

    private final EchoSearchRepository echoSearchRepository;

    public EchoService(EchoRepository echoRepository, EchoMapper echoMapper, EchoSearchRepository echoSearchRepository) {
        this.echoRepository = echoRepository;
        this.echoMapper = echoMapper;
        this.echoSearchRepository = echoSearchRepository;
    }

    /**
     * Save a echo.
     *
     * @param echoDTO the entity to save.
     * @return the persisted entity.
     */
    public EchoDTO save(EchoDTO echoDTO) {
        log.debug("Request to save Echo : {}", echoDTO);
        Echo echo = echoMapper.toEntity(echoDTO);
        echo = echoRepository.save(echo);
        EchoDTO result = echoMapper.toDto(echo);
        echoSearchRepository.save(echo);
        return result;
    }

    /**
     * Partially update a echo.
     *
     * @param echoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EchoDTO> partialUpdate(EchoDTO echoDTO) {
        log.debug("Request to partially update Echo : {}", echoDTO);

        return echoRepository
            .findById(echoDTO.getId())
            .map(
                existingEcho -> {
                    echoMapper.partialUpdate(existingEcho, echoDTO);
                    return existingEcho;
                }
            )
            .map(echoRepository::save)
            .map(
                savedEcho -> {
                    echoSearchRepository.save(savedEcho);

                    return savedEcho;
                }
            )
            .map(echoMapper::toDto);
    }

    /**
     * Get all the echoes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EchoDTO> findAll() {
        log.debug("Request to get all Echoes");
        return echoRepository.findAll().stream().map(echoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one echo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EchoDTO> findOne(Long id) {
        log.debug("Request to get Echo : {}", id);
        return echoRepository.findById(id).map(echoMapper::toDto);
    }

    /**
     * Delete the echo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Echo : {}", id);
        echoRepository.deleteById(id);
        echoSearchRepository.deleteById(id);
    }

    /**
     * Search for the echo corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EchoDTO> search(String query) {
        log.debug("Request to search Echoes for query {}", query);
        return StreamSupport
            .stream(echoSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(echoMapper::toDto)
            .collect(Collectors.toList());
    }
}
