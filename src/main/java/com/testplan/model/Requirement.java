package com.testplan.model;

import java.util.List;
import java.util.ArrayList;

public class Requirement {
    private String id;
    private String title;
    private String description;
    private String priority;
    private String category;
    private List<String> acceptanceCriteria;
    private List<String> dependencies;
    
    public Requirement() {
        this.acceptanceCriteria = new ArrayList<>();
        this.dependencies = new ArrayList<>();
    }
    
    public Requirement(String id, String title, String description, String priority) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public List<String> getAcceptanceCriteria() { return acceptanceCriteria; }
    public void setAcceptanceCriteria(List<String> acceptanceCriteria) { 
        this.acceptanceCriteria = acceptanceCriteria; 
    }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { 
        this.dependencies = dependencies; 
    }
    
    @Override
    public String toString() {
        return String.format("Requirement{id='%s', title='%s', priority='%s'}", 
                           id, title, priority);
    }
}