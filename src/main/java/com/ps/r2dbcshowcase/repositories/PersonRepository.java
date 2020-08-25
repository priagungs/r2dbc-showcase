package com.ps.r2dbcshowcase.repositories;

import com.ps.r2dbcshowcase.entities.Person;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PersonRepository extends ReactiveCrudRepository<Person, Integer> {
  
}
