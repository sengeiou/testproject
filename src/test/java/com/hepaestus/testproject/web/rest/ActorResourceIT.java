package com.hepaestus.testproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hepaestus.testproject.IntegrationTest;
import com.hepaestus.testproject.domain.Actor;
import com.hepaestus.testproject.domain.enumeration.AddressType;
import com.hepaestus.testproject.repository.ActorRepository;
import com.hepaestus.testproject.repository.search.ActorSearchRepository;
import com.hepaestus.testproject.service.ActorService;
import com.hepaestus.testproject.service.dto.ActorDTO;
import com.hepaestus.testproject.service.mapper.ActorMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ActorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ActorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final AddressType DEFAULT_TYPE = AddressType.DEFAULT;
    private static final AddressType UPDATED_TYPE = AddressType.HOME;

    private static final String ENTITY_API_URL = "/api/actors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/actors";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActorRepository actorRepository;

    @Mock
    private ActorRepository actorRepositoryMock;

    @Autowired
    private ActorMapper actorMapper;

    @Mock
    private ActorService actorServiceMock;

    /**
     * This repository is mocked in the com.hepaestus.testproject.repository.search test package.
     *
     * @see com.hepaestus.testproject.repository.search.ActorSearchRepositoryMockConfiguration
     */
    @Autowired
    private ActorSearchRepository mockActorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActorMockMvc;

    private Actor actor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createEntity(EntityManager em) {
        Actor actor = new Actor().name(DEFAULT_NAME).firstName(DEFAULT_FIRST_NAME).lastName(DEFAULT_LAST_NAME).type(DEFAULT_TYPE);
        return actor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createUpdatedEntity(EntityManager em) {
        Actor actor = new Actor().name(UPDATED_NAME).firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).type(UPDATED_TYPE);
        return actor;
    }

    @BeforeEach
    public void initTest() {
        actor = createEntity(em);
    }

    @Test
    @Transactional
    void createActor() throws Exception {
        int databaseSizeBeforeCreate = actorRepository.findAll().size();
        // Create the Actor
        ActorDTO actorDTO = actorMapper.toDto(actor);
        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actorDTO)))
            .andExpect(status().isCreated());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate + 1);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testActor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testActor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testActor.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(1)).save(testActor);
    }

    @Test
    @Transactional
    void createActorWithExistingId() throws Exception {
        // Create the Actor with an existing ID
        actor.setId(1L);
        ActorDTO actorDTO = actorMapper.toDto(actor);

        int databaseSizeBeforeCreate = actorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void getAllActors() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList
        restActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllActorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(actorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restActorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(actorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllActorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(actorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restActorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(actorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get the actor
        restActorMockMvc
            .perform(get(ENTITY_API_URL_ID, actor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(actor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingActor() throws Exception {
        // Get the actor
        restActorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor
        Actor updatedActor = actorRepository.findById(actor.getId()).get();
        // Disconnect from session so that the updates on updatedActor are not directly saved in db
        em.detach(updatedActor);
        updatedActor.name(UPDATED_NAME).firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).type(UPDATED_TYPE);
        ActorDTO actorDTO = actorMapper.toDto(updatedActor);

        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actorDTO))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testActor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testActor.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository).save(testActor);
    }

    @Test
    @Transactional
    void putNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // Create the Actor
        ActorDTO actorDTO = actorMapper.toDto(actor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actorDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void putWithIdMismatchActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // Create the Actor
        ActorDTO actorDTO = actorMapper.toDto(actor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // Create the Actor
        ActorDTO actorDTO = actorMapper.toDto(actor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void partialUpdateActorWithPatch() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor using partial update
        Actor partialUpdatedActor = new Actor();
        partialUpdatedActor.setId(actor.getId());

        partialUpdatedActor.name(UPDATED_NAME).lastName(UPDATED_LAST_NAME).type(UPDATED_TYPE);

        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testActor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testActor.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateActorWithPatch() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor using partial update
        Actor partialUpdatedActor = new Actor();
        partialUpdatedActor.setId(actor.getId());

        partialUpdatedActor.name(UPDATED_NAME).firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).type(UPDATED_TYPE);

        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActor.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testActor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testActor.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // Create the Actor
        ActorDTO actorDTO = actorMapper.toDto(actor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, actorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // Create the Actor
        ActorDTO actorDTO = actorMapper.toDto(actor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setId(count.incrementAndGet());

        // Create the Actor
        ActorDTO actorDTO = actorMapper.toDto(actor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(actorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void deleteActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeDelete = actorRepository.findAll().size();

        // Delete the actor
        restActorMockMvc
            .perform(delete(ENTITY_API_URL_ID, actor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(1)).deleteById(actor.getId());
    }

    @Test
    @Transactional
    void searchActor() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        actorRepository.saveAndFlush(actor);
        when(mockActorSearchRepository.search(queryStringQuery("id:" + actor.getId()))).thenReturn(Collections.singletonList(actor));

        // Search the actor
        restActorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + actor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(actor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
}
