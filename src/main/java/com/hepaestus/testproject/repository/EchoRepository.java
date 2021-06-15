package com.hepaestus.testproject.repository;

import com.hepaestus.testproject.domain.Echo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Echo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EchoRepository extends JpaRepository<Echo, Long> {}
