package controller;

import model.Vehicle;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing vehicle operations.
 * Handles CRUD operations for vehicles in the rental system.
 */
public class VehicleController {
    private List<Vehicle> vehicles;
    private int nextVehicleId;
    
    // Listeners for data changes
    private List<VehicleDataListener> listeners;
    
    /**
     * Interface for listening to vehicle data changes
     */
    public interface VehicleDataListener {
        void onVehicleDataChanged();
    }
    
    public VehicleController() {
        this.vehicles = new ArrayList<>();
        this.nextVehicleId = 1;
        this.listeners = new ArrayList<>();
        initializeDefaultVehicles();
    }
    
    /**
     * Add a listener for vehicle data changes
     */
    public void addVehicleDataListener(VehicleDataListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a listener for vehicle data changes
     */
    public void removeVehicleDataListener(VehicleDataListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners that vehicle data has changed
     */
    private void notifyDataChanged() {
        for (VehicleDataListener listener : listeners) {
            try {
                listener.onVehicleDataChanged();
            } catch (Exception e) {
                // Ignore listener errors
            }
        }
    }
    
    /**
     * Initialize some sample vehicles for demonstration.
     */
    private void initializeDefaultVehicles() {
    vehicles.add(new Vehicle("V001", "Toyota Camry", "Car", 50.0, "Available"));
    vehicles.add(new Vehicle("V002", "Honda Civic", "Car", 45.0, "Available"));
    vehicles.add(new Vehicle("V003", "Yamaha NMAX", "Motorbike", 25.0, "Available"));
    vehicles.add(new Vehicle("V004", "Ford F-150", "Truck", 80.0, "Available"));
    vehicles.add(new Vehicle("V005", "Tesla Model 3", "Car", 100.0, "Rented"));
    vehicles.add(new Vehicle("V006", "Kawasaki Ninja", "Motorbike", 35.0, "Available"));
    vehicles.add(new Vehicle("V007", "Isuzu D-Max", "Truck", 75.0, "Available"));
    vehicles.add(new Vehicle("V008", "Toyota Corolla", "Car", 48.0, "Available"));
    vehicles.add(new Vehicle("V009", "Honda Accord", "Car", 65.0, "Available"));
    vehicles.add(new Vehicle("V010", "Suzuki Hayate", "Motorbike", 20.0, "Under Maintenance"));
    vehicles.add(new Vehicle("V011", "Chevrolet Silverado", "Truck", 85.0, "Available"));
    vehicles.add(new Vehicle("V012", "Toyota Hilux", "Truck", 70.0, "Available"));
    vehicles.add(new Vehicle("V013", "Nissan Altima", "Car", 55.0, "Rented"));
    vehicles.add(new Vehicle("V014", "Kawasaki Z650", "Motorbike", 40.0, "Available"));
    vehicles.add(new Vehicle("V015", "Ford Mustang", "Car", 120.0, "Available"));
    vehicles.add(new Vehicle("V016", "Ford Ranger", "Truck", 78.0, "Available"));
    vehicles.add(new Vehicle("V017", "Yamaha XMAX", "Motorbike", 30.0, "Available"));
    vehicles.add(new Vehicle("V018", "Hyundai Elantra", "Car", 42.0, "Available"));
    vehicles.add(new Vehicle("V019", "Honda PCX", "Motorbike", 28.0, "Under Maintenance"));
    vehicles.add(new Vehicle("V020", "Chevrolet Colorado", "Truck", 72.0, "Available"));
    
    nextVehicleId = 21;
}
    
    /**
     * Add a new vehicle to the system.
     * @param name Vehicle name
     * @param type Vehicle type (Car, Motorbike, Truck)
     * @param pricePerDay Price per day rental rate
     * @param status Vehicle status (Available, Rented, Under Maintenance)
     * @return true if vehicle added successfully
     */
    public boolean addVehicle(String name, String type, double pricePerDay, String status) {
        String vehicleId = String.format("V%03d", nextVehicleId++);
        Vehicle vehicle = new Vehicle(vehicleId, name, type, pricePerDay, status);
        vehicles.add(vehicle);
        notifyDataChanged();
        return true;
    }
    
    /**
     * Add a new vehicle with default status "Available".
     * @param name Vehicle name
     * @param type Vehicle type (Car, Motorbike, Truck)
     * @param pricePerDay Price per day rental rate
     * @return true if vehicle added successfully
     */
    public boolean addVehicle(String name, String type, double pricePerDay) {
        return addVehicle(name, type, pricePerDay, "Available");
    }
    
    /**
     * Update an existing vehicle's information.
     * @param vehicleId ID of the vehicle to update
     * @param name New vehicle name
     * @param type New vehicle type
     * @param pricePerDay New price per day
     * @param status New vehicle status (Available, Rented, Under Maintenance)
     * @return true if update successful, false if vehicle not found
     */
    public boolean updateVehicle(String vehicleId, String name, String type, double pricePerDay, String status) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId().equals(vehicleId)) {
                vehicle.setVehicleName(name);
                vehicle.setVehicleType(type);
                vehicle.setPricePerDay(pricePerDay);
                vehicle.setStatus(status);
                notifyDataChanged();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Update an existing vehicle's information without changing status.
     * @param vehicleId ID of the vehicle to update
     * @param name New vehicle name
     * @param type New vehicle type
     * @param pricePerDay New price per day
     * @return true if update successful, false if vehicle not found
     */
    public boolean updateVehicle(String vehicleId, String name, String type, double pricePerDay) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId().equals(vehicleId)) {
                vehicle.setVehicleName(name);
                vehicle.setVehicleType(type);
                vehicle.setPricePerDay(pricePerDay);
                notifyDataChanged();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Delete a vehicle from the system.
     * @param vehicleId ID of the vehicle to delete
     * @return true if deletion successful, false if vehicle not found
     */
    public boolean deleteVehicle(String vehicleId) {
        boolean removed = vehicles.removeIf(vehicle -> vehicle.getVehicleId().equals(vehicleId));
        if (removed) {
            notifyDataChanged();
        }
        return removed;
    }
    
    /**
     * Get a vehicle by its ID.
     * @param vehicleId ID of the vehicle to find
     * @return Vehicle object if found, null otherwise
     */
    public Vehicle getVehicleById(String vehicleId) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId().equals(vehicleId)) {
                return vehicle;
            }
        }
        return null;
    }
    
    /**
     * Get all vehicles in the system.
     * @return List of all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles);
    }
    
    /**
     * Get all available vehicles (Available or Under Maintenance).
     * @return List of available vehicles
     */
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> available = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if ("Available".equals(vehicle.getStatus()) || "Under Maintenance".equals(vehicle.getStatus())) {
                available.add(vehicle);
            }
        }
        return available;
    }
    
    /**
     * Get vehicles filtered by type.
     * @param type Vehicle type to filter by
     * @return List of vehicles of the specified type
     */
    public List<Vehicle> getVehiclesByType(String type) {
        List<Vehicle> filtered = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleType().equals(type)) {
                filtered.add(vehicle);
            }
        }
        return filtered;
    }
    
    /**
     * Search vehicles by name (case-insensitive).
     * @param nameSearch Search term for vehicle name
     * @return List of matching vehicles
     */
    public List<Vehicle> searchVehiclesByName(String nameSearch) {
        List<Vehicle> results = new ArrayList<>();
        String searchLower = nameSearch.toLowerCase();
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleName().toLowerCase().contains(searchLower)) {
                results.add(vehicle);
            }
        }
        return results;
    }
    
    /**
     * Update vehicle status.
     * @param vehicleId ID of the vehicle
     * @param status New status ("Available" or "Rented")
     * @return true if update successful
     */
    public boolean updateVehicleStatus(String vehicleId, String status) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleId().equals(vehicleId)) {
                vehicle.setStatus(status);
                notifyDataChanged();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get count of available vehicles (Available or Under Maintenance).
     * @return Number of available vehicles
     */
    public int getAvailableCount() {
        int count = 0;
        for (Vehicle vehicle : vehicles) {
            if ("Available".equals(vehicle.getStatus()) || "Under Maintenance".equals(vehicle.getStatus())) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Reset vehicles to default state.
     */
    public void resetVehicles() {
        vehicles.clear();
        nextVehicleId = 1;
        initializeDefaultVehicles();
    }
}