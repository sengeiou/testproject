package com.hepaestus.testproject.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testproject.repository.CardRepository;
import com.hepaestus.testproject.service.CardService;
import com.hepaestus.testproject.service.dto.CardDTO;
import com.hepaestus.testproject.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.hepaestus.testproject.domain.Card}.
 */
@RestController
@RequestMapping("/api")
public class CardResource {

    private final Logger log = LoggerFactory.getLogger(CardResource.class);

    private static final String ENTITY_NAME = "card";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CardService cardService;

    private final CardRepository cardRepository;

    public CardResource(CardService cardService, CardRepository cardRepository) {
        this.cardService = cardService;
        this.cardRepository = cardRepository;
    }

    /**
     * {@code POST  /cards} : Create a new card.
     *
     * @param cardDTO the cardDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cardDTO, or with status {@code 400 (Bad Request)} if the card has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cards")
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardDTO cardDTO) throws URISyntaxException {
        log.debug("REST request to save Card : {}", cardDTO);
        if (cardDTO.getId() != null) {
            throw new BadRequestAlertException("A new card cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CardDTO result = cardService.save(cardDTO);
        return ResponseEntity
            .created(new URI("/api/cards/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cards/:id} : Updates an existing card.
     *
     * @param id the id of the cardDTO to save.
     * @param cardDTO the cardDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cardDTO,
     * or with status {@code 400 (Bad Request)} if the cardDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cardDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cards/{id}")
    public ResponseEntity<CardDTO> updateCard(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CardDTO cardDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Card : {}, {}", id, cardDTO);
        if (cardDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cardDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cardRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CardDTO result = cardService.save(cardDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cardDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cards/:id} : Partial updates given fields of an existing card, field will ignore if it is null
     *
     * @param id the id of the cardDTO to save.
     * @param cardDTO the cardDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cardDTO,
     * or with status {@code 400 (Bad Request)} if the cardDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cardDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cardDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cards/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<CardDTO> partialUpdateCard(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CardDTO cardDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Card partially : {}, {}", id, cardDTO);
        if (cardDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cardDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cardRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CardDTO> result = cardService.partialUpdate(cardDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cardDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cards} : get all the cards.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cards in body.
     */
    @GetMapping("/cards")
    public List<CardDTO> getAllCards() {
        log.debug("REST request to get all Cards");
        return cardService.findAll();
    }

    /**
     * {@code GET  /cards/:id} : get the "id" card.
     *
     * @param id the id of the cardDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cardDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cards/{id}")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        log.debug("REST request to get Card : {}", id);
        Optional<CardDTO> cardDTO = cardService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cardDTO);
    }

    /**
     * {@code DELETE  /cards/:id} : delete the "id" card.
     *
     * @param id the id of the cardDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        log.debug("REST request to delete Card : {}", id);
        cardService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/cards?query=:query} : search for the card corresponding
     * to the query.
     *
     * @param query the query of the card search.
     * @return the result of the search.
     */
    @GetMapping("/_search/cards")
    public List<CardDTO> searchCards(@RequestParam String query) {
        log.debug("REST request to search Cards for query {}", query);
        return cardService.search(query);
    }
}
