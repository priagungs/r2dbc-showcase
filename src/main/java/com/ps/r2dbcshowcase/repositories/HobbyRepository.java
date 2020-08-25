package com.ps.r2dbcshowcase.repositories;

import com.ps.r2dbcshowcase.entities.Hobby;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface HobbyRepository extends ReactiveCrudRepository<Hobby, Integer> {
}
