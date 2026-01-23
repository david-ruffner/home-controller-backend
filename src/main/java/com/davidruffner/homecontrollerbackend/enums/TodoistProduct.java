package com.davidruffner.homecontrollerbackend.enums;

public enum TodoistProduct {
    TASKS("tasks", "/api/v1/tasks"),
    PROJECTS("projects", "/api/v1/projects"),
    FILTERED_TASKS("filtered-tasks", "/api/v1/tasks/filter");

    private final String label;
    private final String path;

    TodoistProduct(String label, String path) {
        this.label = label;
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return this.label;
    }
}