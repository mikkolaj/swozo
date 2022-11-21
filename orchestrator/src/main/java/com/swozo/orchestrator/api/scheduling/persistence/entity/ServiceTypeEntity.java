package com.swozo.orchestrator.api.scheduling.persistence.entity;

public enum ServiceTypeEntity {
    JUPYTER,
    DOCKER,
    QUIZAPP;

    @Override
    public String toString() {
        return switch (this) {
            case DOCKER -> "Docker";
            case JUPYTER -> "Jupyter Notebook";
            case QUIZAPP -> "QuizApp";
        };
    }
}
