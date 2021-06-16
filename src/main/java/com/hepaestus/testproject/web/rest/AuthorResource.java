package com.hepaestus.testproject.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testproject.repository.AuthorRepository;
import com.hepaestus.testproject.service.AuthorService;
import com.hepaestus.testproject.service.dto.AuthorDTO;
import com.hepaestus.testproject.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.hepaestus.testproject.domain.Author}.
 */
@RestController
@RequestMapping("/api")
public class AuthorResource {

    private final Logger log = LoggerFactory.getLogger(AuthorResource.class);

    private static final String ENTITY_NAME = "author";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuthorService authorService;

    private final AuthorRepository authorRepository;

    public AuthorResource(AuthorService authorService, AuthorRepository authorRepository) {
        this.authorService = authorService;
        this.authorRepository = authorRepository;
    }

    /**
     * {@code POST  /authors} : Create a new author.
     *
     * @param authorDTO the authorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new authorDTO, or with status {@code 400 (Bad Request)} if the author has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/authors")
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody AuthorDTO authorDTO) throws URISyntaxException {
        log.debug("REST request to save Author : {}", authorDTO);
        if (authorDTO.getId() != null) {
            throw new BadRequestAlertException("A new author cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AuthorDTO result = authorService.save(authorDTO);
        return ResponseEntity
            .created(new URI("/api/authors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /authors/:id} : Updates an existing author.
     *
     * @param id the id of the authorDTO to save.
     * @param authorDTO the authorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated authorDTO,
     * or with status {@code 400 (Bad Request)} if the authorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the authorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/authors/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AuthorDTO authorDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Author : {}, {}", id, authorDTO);
        if (authorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, authorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!authorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AuthorDTO result = authorService.save(authorDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, authorDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /authors/:id} : Partial updates given fields of an existing author, field will ignore if it is null
     *
     * @param id the id of the authorDTO to save.
     * @param authorDTO the authorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated authorDTO,
     * or with status {@code 400 (Bad Request)} if the authorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the authorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the authorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/authors/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<AuthorDTO> partialUpdateAuthor(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AuthorDTO authorDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Author partially : {}, {}", id, authorDTO);
        if (authorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, authorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!authorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AuthorDTO> result = authorService.partialUpdate(authorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, authorDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /authors} : get all the authors.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of authors in body.
     */
    @GetMapping("/authors")
    public List<AuthorDTO> getAllAuthors(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Authors");
        return authorService.findAll();
    }

    /**
     * {@code GET  /authors/:id} : get the "id" author.
     *
     * @param id the id of the authorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/authors/{id}")
    public ResponseEntity<AuthorDTO> getAuthor(@PathVariable Long id) {
        log.debug("REST request to get Author : {}", id);
        Optional<AuthorDTO> authorDTO = authorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(authorDTO);
    }

    /**
     * {@code DELETE  /authors/:id} : delete the "id" author.
     *
     * @param id the id of the authorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        log.debug("REST request to delete Author : {}", id);
        authorService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/authors?query=:query} : search for the author corresponding
     * to the query.
     *
     * @param query the query of the author search.
     * @return the result of the search.
     */
    @GetMapping("/_search/authors")
    public List<AuthorDTO> searchAuthors(@RequestParam String query) {
        log.debug("REST request to search Authors for query {}", query);
        return authorService.search(query);
    }
}
