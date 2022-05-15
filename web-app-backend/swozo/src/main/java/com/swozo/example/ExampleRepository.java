package com.swozo.example;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ExampleRepository extends CrudRepository<ExampleModel, Long> {
    Optional<ExampleModel> findByName(String name);
}
