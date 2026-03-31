package model;

/**
 * Represents a vehicle in the Rental System.
 * Each vehicle has unique ID, name, type, price per day, and status.
 */
public class Vehicle {
    private String vehicleId;
    private String vehicleName;
    private String vehicleType; // "Car", "Motorbike", "Truck"
    private double pricePerDay;
    private String status; // "Available", "Rented"
    
    public Vehicle(String vehicleId, String vehicleName, String vehicleType, 
                   double pricePerDay, String status) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.vehicleType = vehicleType;
        this.pricePerDay = pricePerDay;
        this.status = status;
    }
    
    // Getters and Setters
    public String getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public String getVehicleName() {
        return vehicleName;
    }
    
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public double getPricePerDay() {
        return pricePerDay;
    }
    
    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return vehicleId + " - " + vehicleName + " (" + vehicleType + ")";
    }
}

