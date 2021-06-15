package com.hepaestus.testproject.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testproject.domain.Card;
import com.hepaestus.testproject.repository.CardRepository;
import com.hepaestus.testproject.repository.search.CardSearchRepository;
import com.hepaestus.testproject.service.dto.CardDTO;
import com.hepaestus.testproject.service.mapper.CardMapper;
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
 * Service Implementation for managing {@link Card}.
 */
@Service
@Transactional
public class CardService {

    private final Logger log = LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    private final CardSearchRepository cardSearchRepository;

    public CardService(CardRepository cardRepository, CardMapper cardMapper, CardSearchRepository cardSearchRepository) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.cardSearchRepository = cardSearchRepository;
    }

    /**
     * Save a card.
     *
     * @param cardDTO the entity to save.
     * @return the persisted entity.
     */
    public CardDTO save(CardDTO cardDTO) {
        log.debug("Request to save Card : {}", cardDTO);
        Card card = cardMapper.toEntity(cardDTO);
        card = cardRepository.save(card);
        CardDTO result = cardMapper.toDto(card);
        cardSearchRepository.save(card);
        return result;
    }

    /**
     * Partially update a card.
     *
     * @param cardDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CardDTO> partialUpdate(CardDTO cardDTO) {
        log.debug("Request to partially update Card : {}", cardDTO);

        return cardRepository
            .findById(cardDTO.getId())
            .map(
                existingCard -> {
                    cardMapper.partialUpdate(existingCard, cardDTO);
                    return existingCard;
                }
            )
            .map(cardRepository::save)
            .map(
                savedCard -> {
                    cardSearchRepository.save(savedCard);

                    return savedCard;
                }
            )
            .map(cardMapper::toDto);
    }

    /**
     * Get all the cards.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CardDTO> findAll() {
        log.debug("Request to get all Cards");
        return cardRepository.findAll().stream().map(cardMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one card by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CardDTO> findOne(Long id) {
        log.debug("Request to get Card : {}", id);
        return cardRepository.findById(id).map(cardMapper::toDto);
    }

    /**
     * Delete the card by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Card : {}", id);
        cardRepository.deleteById(id);
        cardSearchRepository.deleteById(id);
    }

    /**
     * Search for the card corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CardDTO> search(String query) {
        log.debug("Request to search Cards for query {}", query);
        return StreamSupport
            .stream(cardSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(cardMapper::toDto)
            .collect(Collectors.toList());
    }
}
