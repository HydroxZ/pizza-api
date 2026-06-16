package com.awesomepizza.repository;

import com.awesomepizza.domain.PizzaType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PizzaTypeRepository extends JpaRepository<PizzaType, Long> {
    Optional<PizzaType> findByName(String name);
}
