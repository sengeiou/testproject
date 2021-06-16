package com.hepaestus.testproject.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testproject.repository.EchoRepository;
import com.hepaestus.testproject.service.EchoService;
import com.hepaestus.testproject.service.dto.EchoDTO;
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
 * REST controller for managing {@link com.hepaestus.testproject.domain.Echo}.
 */
@RestController
@RequestMapping("/api")
public class EchoResource {

    private final Logger log = LoggerFactory.getLogger(EchoResource.class);

    private static final String ENTITY_NAME = "echo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EchoService echoService;

    private final EchoRepository echoRepository;

    public EchoResource(EchoService echoService, EchoRepository echoRepository) {
        this.echoService = echoService;
        this.echoRepository = echoRepository;
    }

    /**
     * {@code POST  /echoes} : Create a new echo.
     *
     * @param echoDTO the echoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new echoDTO, or with status {@code 400 (Bad Request)} if the echo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/echoes")
    public ResponseEntity<EchoDTO> createEcho(@Valid @RequestBody EchoDTO echoDTO) throws URISyntaxException {
        log.debug("REST request to save Echo : {}", echoDTO);
        if (echoDTO.getId() != null) {
            throw new BadRequestAlertException("A new echo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EchoDTO result = echoService.save(echoDTO);
        return ResponseEntity
            .created(new URI("/api/echoes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /echoes/:id} : Updates an existing echo.
     *
     * @param id the id of the echoDTO to save.
     * @param echoDTO the echoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated echoDTO,
     * or with status {@code 400 (Bad Request)} if the echoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the echoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/echoes/{id}")
    public ResponseEntity<EchoDTO> updateEcho(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EchoDTO echoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Echo : {}, {}", id, echoDTO);
        if (echoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, echoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!echoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EchoDTO result = echoService.save(echoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, echoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /echoes/:id} : Partial updates given fields of an existing echo, field will ignore if it is null
     *
     * @param id the id of the echoDTO to save.
     * @param echoDTO the echoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated echoDTO,
     * or with status {@code 400 (Bad Request)} if the echoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the echoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the echoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/echoes/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<EchoDTO> partialUpdateEcho(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EchoDTO echoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Echo partially : {}, {}", id, echoDTO);
        if (echoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, echoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!echoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EchoDTO> result = echoService.partialUpdate(echoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, echoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /echoes} : get all the echoes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of echoes in body.
     */
    @GetMapping("/echoes")
    public List<EchoDTO> getAllEchoes() {
        log.debug("REST request to get all Echoes");
        return echoService.findAll();
    }

    /**
     * {@code GET  /echoes/:id} : get the "id" echo.
     *
     * @param id the id of the echoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the echoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/echoes/{id}")
    public ResponseEntity<EchoDTO> getEcho(@PathVariable Long id) {
        log.debug("REST request to get Echo : {}", id);
        Optional<EchoDTO> echoDTO = echoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(echoDTO);
    }

    /**
     * {@code DELETE  /echoes/:id} : delete the "id" echo.
     *
     * @param id the id of the echoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/echoes/{id}")
    public ResponseEntity<Void> deleteEcho(@PathVariable Long id) {
        log.debug("REST request to delete Echo : {}", id);
        echoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/echoes?query=:query} : search for the echo corresponding
     * to the query.
     *
     * @param query the query of the echo search.
     * @return the result of the search.
     */
    @GetMapping("/_search/echoes")
    public List<EchoDTO> searchEchoes(@RequestParam String query) {
        log.debug("REST request to search Echoes for query {}", query);
        return echoService.search(query);
    }
}
