package com.hepaestus.testproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hepaestus.testproject.IntegrationTest;
import com.hepaestus.testproject.domain.Dog;
import com.hepaestus.testproject.repository.DogRepository;
import com.hepaestus.testproject.repository.search.DogSearchRepository;
import com.hepaestus.testproject.service.dto.DogDTO;
import com.hepaestus.testproject.service.mapper.DogMapper;
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
 * Integration tests for the {@link DogResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DogResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/dogs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/dogs";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private DogMapper dogMapper;

    /**
     * This repository is mocked in the com.hepaestus.testproject.repository.search test package.
     *
     * @see com.hepaestus.testproject.repository.search.DogSearchRepositoryMockConfiguration
     */
    @Autowired
    private DogSearchRepository mockDogSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDogMockMvc;

    private Dog dog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dog createEntity(EntityManager em) {
        Dog dog = new Dog().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).created(DEFAULT_CREATED);
        return dog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dog createUpdatedEntity(EntityManager em) {
        Dog dog = new Dog().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).created(UPDATED_CREATED);
        return dog;
    }

    @BeforeEach
    public void initTest() {
        dog = createEntity(em);
    }

    @Test
    @Transactional
    void createDog() throws Exception {
        int databaseSizeBeforeCreate = dogRepository.findAll().size();
        // Create the Dog
        DogDTO dogDTO = dogMapper.toDto(dog);
        restDogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dogDTO)))
            .andExpect(status().isCreated());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeCreate + 1);
        Dog testDog = dogList.get(dogList.size() - 1);
        assertThat(testDog.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDog.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testDog.getCreated()).isEqualTo(DEFAULT_CREATED);

        // Validate the Dog in Elasticsearch
        verify(mockDogSearchRepository, times(1)).save(testDog);
    }

    @Test
    @Transactional
    void createDogWithExistingId() throws Exception {
        // Create the Dog with an existing ID
        dog.setId(1L);
        DogDTO dogDTO = dogMapper.toDto(dog);

        int databaseSizeBeforeCreate = dogRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeCreate);

        // Validate the Dog in Elasticsearch
        verify(mockDogSearchRepository, times(0)).save(dog);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dogRepository.findAll().size();
        // set the field null
        dog.setName(null);

        // Create the Dog, which fails.
        DogDTO dogDTO = dogMapper.toDto(dog);

        restDogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dogDTO)))
            .andExpect(status().isBadRequest());

        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDogs() throws Exception {
        // Initialize the database
        dogRepository.saveAndFlush(dog);

        // Get all the dogList
        restDogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dog.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())));
    }

    @Test
    @Transactional
    void getDog() throws Exception {
        // Initialize the database
        dogRepository.saveAndFlush(dog);

        // Get the dog
        restDogMockMvc
            .perform(get(ENTITY_API_URL_ID, dog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dog.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDog() throws Exception {
        // Get the dog
        restDogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDog() throws Exception {
        // Initialize the database
        dogRepository.saveAndFlush(dog);

        int databaseSizeBeforeUpdate = dogRepository.findAll().size();

        // Update the dog
        Dog updatedDog = dogRepository.findById(dog.getId()).get();
        // Disconnect from session so that the updates on updatedDog are not directly saved in db
        em.detach(updatedDog);
        updatedDog.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).created(UPDATED_CREATED);
        DogDTO dogDTO = dogMapper.toDto(updatedDog);

        restDogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dogDTO))
            )
            .andExpect(status().isOk());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeUpdate);
        Dog testDog = dogList.get(dogList.size() - 1);
        assertThat(testDog.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDog.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDog.getCreated()).isEqualTo(UPDATED_CREATED);

        // Validate the Dog in Elasticsearch
        verify(mockDogSearchRepository).save(testDog);
    }

    @Test
    @Transactional
    void putNonExistingDog() throws Exception {
        int databaseSizeBeforeUpdate = dogRepository.findAll().size();
        dog.setId(count.incrementAndGet());

        // Create the Dog
        DogDTO dogDTO = dogMapper.toDto(dog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dog in Elasticsearch
        verify(mockDogSearchRepository, times(0)).save(dog);
    }

    @Test
    @Transactional
    void putWithIdMismatchDog() throws Exception {
        int databaseSizeBeforeUpdate = dogRepository.findAll().size();
        dog.setId(count.incrementAndGet());

        // Create the Dog
        DogDTO dogDTO = dogMapper.toDto(dog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dog in Elasticsearch
        verify(mockDogSearchRepository, times(0)).save(dog);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDog() throws Exception {
        int databaseSizeBeforeUpdate = dogRepository.findAll().size();
        dog.setId(count.incrementAndGet());

        // Create the Dog
        DogDTO dogDTO = dogMapper.toDto(dog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dog in Elasticsearch
        verify(mockDogSearchRepository, times(0)).save(dog);
    }

    @Test
    @Transactional
    void partialUpdateDogWithPatch() throws Exception {
        // Initialize the database
        dogRepository.saveAndFlush(dog);

        int databaseSizeBeforeUpdate = dogRepository.findAll().size();

        // Update the dog using partial update
        Dog partialUpdatedDog = new Dog();
        partialUpdatedDog.setId(dog.getId());

        partialUpdatedDog.description(UPDATED_DESCRIPTION).created(UPDATED_CREATED);

        restDogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDog))
            )
            .andExpect(status().isOk());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeUpdate);
        Dog testDog = dogList.get(dogList.size() - 1);
        assertThat(testDog.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDog.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDog.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    void fullUpdateDogWithPatch() throws Exception {
        // Initialize the database
        dogRepository.saveAndFlush(dog);

        int databaseSizeBeforeUpdate = dogRepository.findAll().size();

        // Update the dog using partial update
        Dog partialUpdatedDog = new Dog();
        partialUpdatedDog.setId(dog.getId());

        partialUpdatedDog.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).created(UPDATED_CREATED);

        restDogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDog))
            )
            .andExpect(status().isOk());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeUpdate);
        Dog testDog = dogList.get(dogList.size() - 1);
        assertThat(testDog.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDog.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDog.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    void patchNonExistingDog() throws Exception {
        int databaseSizeBeforeUpdate = dogRepository.findAll().size();
        dog.setId(count.incrementAndGet());

        // Create the Dog
        DogDTO dogDTO = dogMapper.toDto(dog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dog in Elasticsearch
        verify(mockDogSearchRepository, times(0)).save(dog);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDog() throws Exception {
        int databaseSizeBeforeUpdate = dogRepository.findAll().size();
        dog.setId(count.incrementAndGet());

        // Create the Dog
        DogDTO dogDTO = dogMapper.toDto(dog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dog in Elasticsearch
        verify(mockDogSearchRepository, times(0)).save(dog);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDog() throws Exception {
        int databaseSizeBeforeUpdate = dogRepository.findAll().size();
        dog.setId(count.incrementAndGet());

        // Create the Dog
        DogDTO dogDTO = dogMapper.toDto(dog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dog in the database
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Dog in Elasticsearch
        verify(mockDogSearchRepository, times(0)).save(dog);
    }

    @Test
    @Transactional
    void deleteDog() throws Exception {
        // Initialize the database
        dogRepository.saveAndFlush(dog);

        int databaseSizeBeforeDelete = dogRepository.findAll().size();

        // Delete the dog
        restDogMockMvc.perform(delete(ENTITY_API_URL_ID, dog.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Dog> dogList = dogRepository.findAll();
        assertThat(dogList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Dog in Elasticsearch
        verify(mockDogSearchRepository, times(1)).deleteById(dog.getId());
    }

    @Test
    @Transactional
    void searchDog() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        dogRepository.saveAndFlush(dog);
        when(mockDogSearchRepository.search(queryStringQuery("id:" + dog.getId()))).thenReturn(Collections.singletonList(dog));

        // Search the dog
        restDogMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + dog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dog.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())));
    }
}
