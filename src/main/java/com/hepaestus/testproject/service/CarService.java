package com.hepaestus.testproject.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.hepaestus.testproject.domain.Car;
import com.hepaestus.testproject.repository.CarRepository;
import com.hepaestus.testproject.repository.search.CarSearchRepository;
import com.hepaestus.testproject.service.dto.CarDTO;
import com.hepaestus.testproject.service.mapper.CarMapper;
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
 * Service Implementation for managing {@link Car}.
 */
@Service
@Transactional
public class CarService {

    private final Logger log = LoggerFactory.getLogger(CarService.class);

    private final CarRepository carRepository;

    private final CarMapper carMapper;

    private final CarSearchRepository carSearchRepository;

    public CarService(CarRepository carRepository, CarMapper carMapper, CarSearchRepository carSearchRepository) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
        this.carSearchRepository = carSearchRepository;
    }

    /**
     * Save a car.
     *
     * @param carDTO the entity to save.
     * @return the persisted entity.
     */
    public CarDTO save(CarDTO carDTO) {
        log.debug("Request to save Car : {}", carDTO);
        Car car = carMapper.toEntity(carDTO);
        car = carRepository.save(car);
        CarDTO result = carMapper.toDto(car);
        carSearchRepository.save(car);
        return result;
    }

    /**
     * Partially update a car.
     *
     * @param carDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CarDTO> partialUpdate(CarDTO carDTO) {
        log.debug("Request to partially update Car : {}", carDTO);

        return carRepository
            .findById(carDTO.getId())
            .map(
                existingCar -> {
                    carMapper.partialUpdate(existingCar, carDTO);
                    return existingCar;
                }
            )
            .map(carRepository::save)
            .map(
                savedCar -> {
                    carSearchRepository.save(savedCar);

                    return savedCar;
                }
            )
            .map(carMapper::toDto);
    }

    /**
     * Get all the cars.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CarDTO> findAll() {
        log.debug("Request to get all Cars");
        return carRepository.findAll().stream().map(carMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one car by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CarDTO> findOne(Long id) {
        log.debug("Request to get Car : {}", id);
        return carRepository.findById(id).map(carMapper::toDto);
    }

    /**
     * Delete the car by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Car : {}", id);
        carRepository.deleteById(id);
        carSearchRepository.deleteById(id);
    }

    /**
     * Search for the car corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CarDTO> search(String query) {
        log.debug("Request to search Cars for query {}", query);
        return StreamSupport
            .stream(carSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(carMapper::toDto)
            .collect(Collectors.toList());
    }
}
