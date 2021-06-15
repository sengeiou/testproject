package com.hepaestus.testproject.repository;

import com.hepaestus.testproject.domain.Dog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Dog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {}
