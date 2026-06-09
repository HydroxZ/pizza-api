package com.awesomepizza.repository;

import com.awesomepizza.domain.PizzaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for PizzaType entity.
 */
@Repository
public interface PizzaTypeRepository extends JpaRepository<PizzaType, Long> {
    // Default implementation sufficient - enum stored in database
}
