package tech.msociety.terawhere.models;

public class Vehicle {
    private String plateNumber;
    private String description;
    private String model;
    
    public Vehicle(String plateNumber, String description, String model) {
        this.plateNumber = plateNumber;
        this.description = description;
        this.model = model;
    }
    
    public String getPlateNumber() {
        return plateNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getModel() {
        return model;
    }
}
