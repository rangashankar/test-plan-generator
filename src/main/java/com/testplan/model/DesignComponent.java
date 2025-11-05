package com.testplan.model;

import java.util.List;
import java.util.ArrayList;

public class DesignComponent {
    private String id;
    private String name;
    private String type; // UI, API, Database, Service, etc.
    private String description;
    private List<String> interfaces;
    private List<String> dependencies;
    private List<String> businessRules;
    
    public DesignComponent() {
        this.interfaces = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.businessRules = new ArrayList<>();
    }
    
    public DesignComponent(String id, String name, String type, String description) {
        this();
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getInterfaces() { return interfaces; }
    public void setInterfaces(List<String> interfaces) { this.interfaces = interfaces; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public List<String> getBusinessRules() { return businessRules; }
    public void setBusinessRules(List<String> businessRules) { this.businessRules = businessRules; }
    
    @Override
    public String toString() {
        return String.format("DesignComponent{id='%s', name='%s', type='%s'}", 
                           id, name, type);
    }
}