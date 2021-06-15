package com.hepaestus.testproject.repository;

import com.hepaestus.testproject.domain.Card;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Card entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {}
