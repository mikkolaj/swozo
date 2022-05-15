package com.swozo.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {
    private final ExampleRepository repo;

    @Autowired
    public ExampleService(ExampleRepository repo) {
        this.repo = repo;
        repo.save(new ExampleModel("example-name"));
    }

    public ExampleModel getExample() {
        return repo.findByName("example-name").orElseThrow();
    }
}
