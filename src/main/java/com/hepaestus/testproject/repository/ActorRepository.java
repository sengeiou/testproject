package com.hepaestus.testproject.repository;

import com.hepaestus.testproject.domain.Actor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Actor entity.
 */
@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
    @Query(
        value = "select distinct actor from Actor actor left join fetch actor.echoes",
        countQuery = "select count(distinct actor) from Actor actor"
    )
    Page<Actor> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct actor from Actor actor left join fetch actor.echoes")
    List<Actor> findAllWithEagerRelationships();

    @Query("select actor from Actor actor left join fetch actor.echoes where actor.id =:id")
    Optional<Actor> findOneWithEagerRelationships(@Param("id") Long id);
}
