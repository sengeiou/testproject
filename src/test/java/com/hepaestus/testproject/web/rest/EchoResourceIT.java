package com.hepaestus.testproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hepaestus.testproject.IntegrationTest;
import com.hepaestus.testproject.domain.Echo;
import com.hepaestus.testproject.repository.EchoRepository;
import com.hepaestus.testproject.repository.search.EchoSearchRepository;
import com.hepaestus.testproject.service.dto.EchoDTO;
import com.hepaestus.testproject.service.mapper.EchoMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EchoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EchoResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/echoes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/echoes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EchoRepository echoRepository;

    @Autowired
    private EchoMapper echoMapper;

    /**
     * This repository is mocked in the com.hepaestus.testproject.repository.search test package.
     *
     * @see com.hepaestus.testproject.repository.search.EchoSearchRepositoryMockConfiguration
     */
    @Autowired
    private EchoSearchRepository mockEchoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEchoMockMvc;

    private Echo echo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Echo createEntity(EntityManager em) {
        Echo echo = new Echo().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).created(DEFAULT_CREATED);
        return echo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Echo createUpdatedEntity(EntityManager em) {
        Echo echo = new Echo().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).created(UPDATED_CREATED);
        return echo;
    }

    @BeforeEach
    public void initTest() {
        echo = createEntity(em);
    }

    @Test
    @Transactional
    void createEcho() throws Exception {
        int databaseSizeBeforeCreate = echoRepository.findAll().size();
        // Create the Echo
        EchoDTO echoDTO = echoMapper.toDto(echo);
        restEchoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(echoDTO)))
            .andExpect(status().isCreated());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeCreate + 1);
        Echo testEcho = echoList.get(echoList.size() - 1);
        assertThat(testEcho.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEcho.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEcho.getCreated()).isEqualTo(DEFAULT_CREATED);

        // Validate the Echo in Elasticsearch
        verify(mockEchoSearchRepository, times(1)).save(testEcho);
    }

    @Test
    @Transactional
    void createEchoWithExistingId() throws Exception {
        // Create the Echo with an existing ID
        echo.setId(1L);
        EchoDTO echoDTO = echoMapper.toDto(echo);

        int databaseSizeBeforeCreate = echoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEchoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(echoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeCreate);

        // Validate the Echo in Elasticsearch
        verify(mockEchoSearchRepository, times(0)).save(echo);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = echoRepository.findAll().size();
        // set the field null
        echo.setName(null);

        // Create the Echo, which fails.
        EchoDTO echoDTO = echoMapper.toDto(echo);

        restEchoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(echoDTO)))
            .andExpect(status().isBadRequest());

        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEchoes() throws Exception {
        // Initialize the database
        echoRepository.saveAndFlush(echo);

        // Get all the echoList
        restEchoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(echo.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())));
    }

    @Test
    @Transactional
    void getEcho() throws Exception {
        // Initialize the database
        echoRepository.saveAndFlush(echo);

        // Get the echo
        restEchoMockMvc
            .perform(get(ENTITY_API_URL_ID, echo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(echo.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEcho() throws Exception {
        // Get the echo
        restEchoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEcho() throws Exception {
        // Initialize the database
        echoRepository.saveAndFlush(echo);

        int databaseSizeBeforeUpdate = echoRepository.findAll().size();

        // Update the echo
        Echo updatedEcho = echoRepository.findById(echo.getId()).get();
        // Disconnect from session so that the updates on updatedEcho are not directly saved in db
        em.detach(updatedEcho);
        updatedEcho.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).created(UPDATED_CREATED);
        EchoDTO echoDTO = echoMapper.toDto(updatedEcho);

        restEchoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, echoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(echoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeUpdate);
        Echo testEcho = echoList.get(echoList.size() - 1);
        assertThat(testEcho.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEcho.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEcho.getCreated()).isEqualTo(UPDATED_CREATED);

        // Validate the Echo in Elasticsearch
        verify(mockEchoSearchRepository).save(testEcho);
    }

    @Test
    @Transactional
    void putNonExistingEcho() throws Exception {
        int databaseSizeBeforeUpdate = echoRepository.findAll().size();
        echo.setId(count.incrementAndGet());

        // Create the Echo
        EchoDTO echoDTO = echoMapper.toDto(echo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEchoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, echoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(echoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Echo in Elasticsearch
        verify(mockEchoSearchRepository, times(0)).save(echo);
    }

    @Test
    @Transactional
    void putWithIdMismatchEcho() throws Exception {
        int databaseSizeBeforeUpdate = echoRepository.findAll().size();
        echo.setId(count.incrementAndGet());

        // Create the Echo
        EchoDTO echoDTO = echoMapper.toDto(echo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEchoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(echoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Echo in Elasticsearch
        verify(mockEchoSearchRepository, times(0)).save(echo);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEcho() throws Exception {
        int databaseSizeBeforeUpdate = echoRepository.findAll().size();
        echo.setId(count.incrementAndGet());

        // Create the Echo
        EchoDTO echoDTO = echoMapper.toDto(echo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEchoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(echoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Echo in Elasticsearch
        verify(mockEchoSearchRepository, times(0)).save(echo);
    }

    @Test
    @Transactional
    void partialUpdateEchoWithPatch() throws Exception {
        // Initialize the database
        echoRepository.saveAndFlush(echo);

        int databaseSizeBeforeUpdate = echoRepository.findAll().size();

        // Update the echo using partial update
        Echo partialUpdatedEcho = new Echo();
        partialUpdatedEcho.setId(echo.getId());

        partialUpdatedEcho.name(UPDATED_NAME).created(UPDATED_CREATED);

        restEchoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEcho.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEcho))
            )
            .andExpect(status().isOk());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeUpdate);
        Echo testEcho = echoList.get(echoList.size() - 1);
        assertThat(testEcho.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEcho.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEcho.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    void fullUpdateEchoWithPatch() throws Exception {
        // Initialize the database
        echoRepository.saveAndFlush(echo);

        int databaseSizeBeforeUpdate = echoRepository.findAll().size();

        // Update the echo using partial update
        Echo partialUpdatedEcho = new Echo();
        partialUpdatedEcho.setId(echo.getId());

        partialUpdatedEcho.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).created(UPDATED_CREATED);

        restEchoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEcho.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEcho))
            )
            .andExpect(status().isOk());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeUpdate);
        Echo testEcho = echoList.get(echoList.size() - 1);
        assertThat(testEcho.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEcho.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEcho.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    void patchNonExistingEcho() throws Exception {
        int databaseSizeBeforeUpdate = echoRepository.findAll().size();
        echo.setId(count.incrementAndGet());

        // Create the Echo
        EchoDTO echoDTO = echoMapper.toDto(echo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEchoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, echoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(echoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Echo in Elasticsearch
        verify(mockEchoSearchRepository, times(0)).save(echo);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEcho() throws Exception {
        int databaseSizeBeforeUpdate = echoRepository.findAll().size();
        echo.setId(count.incrementAndGet());

        // Create the Echo
        EchoDTO echoDTO = echoMapper.toDto(echo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEchoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(echoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Echo in Elasticsearch
        verify(mockEchoSearchRepository, times(0)).save(echo);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEcho() throws Exception {
        int databaseSizeBeforeUpdate = echoRepository.findAll().size();
        echo.setId(count.incrementAndGet());

        // Create the Echo
        EchoDTO echoDTO = echoMapper.toDto(echo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEchoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(echoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Echo in the database
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Echo in Elasticsearch
        verify(mockEchoSearchRepository, times(0)).save(echo);
    }

    @Test
    @Transactional
    void deleteEcho() throws Exception {
        // Initialize the database
        echoRepository.saveAndFlush(echo);

        int databaseSizeBeforeDelete = echoRepository.findAll().size();

        // Delete the echo
        restEchoMockMvc
            .perform(delete(ENTITY_API_URL_ID, echo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Echo> echoList = echoRepository.findAll();
        assertThat(echoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Echo in Elasticsearch
        verify(mockEchoSearchRepository, times(1)).deleteById(echo.getId());
    }

    @Test
    @Transactional
    void searchEcho() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        echoRepository.saveAndFlush(echo);
        when(mockEchoSearchRepository.search(queryStringQuery("id:" + echo.getId()))).thenReturn(Collections.singletonList(echo));

        // Search the echo
        restEchoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + echo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(echo.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())));
    }
}
