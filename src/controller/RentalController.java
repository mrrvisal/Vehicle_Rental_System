package controller;

import model.Rental;
import model.Vehicle;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing rental operations.
 * Handles rental creation, return processing, and history tracking.
 * Rental is now based on date/time instead of days.
 */
public class RentalController {
    private List<Rental> rentals;
    private int nextRentalId;
    private VehicleController vehicleController;
    
    // Listeners for data changes
    private List<RentalDataListener> listeners;
    
    /**
     * Interface for listening to rental data changes
     */
    public interface RentalDataListener {
        void onRentalDataChanged();
    }
    
    public RentalController(VehicleController vehicleController) {
        this.rentals = new ArrayList<>();
        this.nextRentalId = 1001;
        this.vehicleController = vehicleController;
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Add a listener for rental data changes
     */
    public void addRentalDataListener(RentalDataListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a listener for rental data changes
     */
    public void removeRentalDataListener(RentalDataListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners that rental data has changed
     */
    private void notifyDataChanged() {
        for (RentalDataListener listener : listeners) {
            try {
                listener.onRentalDataChanged();
            } catch (Exception e) {
                // Ignore listener errors
            }
        }
    }
    
    /**
     * Rent a vehicle for a customer.
     * @param customerUsername Username of the customer
     * @param vehicleId ID of the vehicle to rent
     * @param rentalDate Start date/time of rental
     * @param expectedReturnDate Expected return date/time
     * @return Rental object if successful, null if vehicle not available
     */
    public Rental rentVehicle(String customerUsername, String vehicleId, 
                              LocalDateTime rentalDate, LocalDateTime expectedReturnDate) {
        // Get vehicle details
        Vehicle vehicle = vehicleController.getVehicleById(vehicleId);
        if (vehicle == null) {
            return null;
        }
        
        // Check if vehicle is available
        if (!"Available".equals(vehicle.getStatus())) {
            return null;
        }
        
        // Validate dates
        if (rentalDate == null || expectedReturnDate == null) {
            return null;
        }
        
        if (expectedReturnDate.isBefore(rentalDate) || expectedReturnDate.equals(rentalDate)) {
            return null;
        }
        
        // Calculate duration in hours
        long hours = ChronoUnit.HOURS.between(rentalDate, expectedReturnDate);
        if (hours <= 0) {
            return null;
        }
        
        // Calculate total cost: pricePerDay / 24 hours * hours rented
        double pricePerHour = vehicle.getPricePerDay() / 24.0;
        double totalCost = pricePerHour * hours;
        
        // Create rental record
        String rentalId = String.format("R%04d", nextRentalId++);
        Rental rental = new Rental(rentalId, customerUsername, vehicleId, 
                                   vehicle.getVehicleName(), totalCost, rentalDate, expectedReturnDate);
        
        // Update vehicle status
        vehicleController.updateVehicleStatus(vehicleId, "Rented");
        
        // Save rental
        rentals.add(rental);
        notifyDataChanged();
        return rental;
    }
    
    /**
     * Return a rented vehicle.
     * @param rentalId ID of the rental to return
     * @return true if return successful, false if rental not found
     */
    public boolean returnVehicle(String rentalId) {
        for (Rental rental : rentals) {
            if (rental.getRentalId().equals(rentalId) && "Active".equals(rental.getStatus())) {
                rental.markAsReturned();
                vehicleController.updateVehicleStatus(rental.getVehicleId(), "Available");
                notifyDataChanged();
                return true;
            }
        }
        return false;
    }

    /**
     * Report a rental as lost and specify expected give-back date.
     * @param rentalId ID of the rental to report as lost
     * @param giveBackDate Expected date when the lost item will be returned
     * @return true if report successful, false if rental not found or not active
     */
    public boolean reportRentalAsLost(String rentalId, LocalDateTime giveBackDate) {
        for (Rental rental : rentals) {
            if (rental.getRentalId().equals(rentalId) && "Active".equals(rental.getStatus())) {
                rental.markAsLost(giveBackDate);
                vehicleController.updateVehicleStatus(rental.getVehicleId(), "Lost");
                notifyDataChanged();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get rental by ID.
     * @param rentalId ID of the rental
     * @return Rental object if found, null otherwise
     */
    public Rental getRentalById(String rentalId) {
        for (Rental rental : rentals) {
            if (rental.getRentalId().equals(rentalId)) {
                return rental;
            }
        }
        return null;
    }
    
    /**
     * Get all rentals in the system.
     * @return List of all rentals
     */
    public List<Rental> getAllRentals() {
        return new ArrayList<>(rentals);
    }
    
    /**
     * Get all active rentals.
     * @return List of active rentals
     */
    public List<Rental> getActiveRentals() {
        List<Rental> active = new ArrayList<>();
        for (Rental rental : rentals) {
            if ("Active".equals(rental.getStatus())) {
                active.add(rental);
            }
        }
        return active;
    }
    
    /**
     * Get all rentals for a specific customer.
     * @param customerUsername Username of the customer
     * @return List of rentals for the customer
     */
    public List<Rental> getRentalsByCustomer(String customerUsername) {
        List<Rental> customerRentals = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getCustomerUsername().equals(customerUsername)) {
                customerRentals.add(rental);
            }
        }
        return customerRentals;
    }
    
    /**
     * Get active rental for a specific vehicle.
     * @param vehicleId ID of the vehicle
     * @return Rental object if vehicle is currently rented, null otherwise
     */
    public Rental getActiveRentalForVehicle(String vehicleId) {
        for (Rental rental : rentals) {
            if (rental.getVehicleId().equals(vehicleId) && "Active".equals(rental.getStatus())) {
                return rental;
            }
        }
        return null;
    }
    
    /**
     * Get active rentals for a specific customer.
     * @param customerUsername Username of the customer
     * @return List of active rentals for the customer
     */
    public List<Rental> getActiveRentalsByCustomer(String customerUsername) {
        List<Rental> active = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getCustomerUsername().equals(customerUsername) && 
                "Active".equals(rental.getStatus())) {
                active.add(rental);
            }
        }
        return active;
    }
    
    /**
     * Calculate total revenue from all returned rentals.
     * @return Total revenue amount
     */
    public double getTotalRevenue() {
        double total = 0;
        for (Rental rental : rentals) {
            if ("Returned".equals(rental.getStatus())) {
                total += rental.getTotalCost();
            }
        }
        return total;
    }
    
    /**
     * Get count of total rentals.
     * @return Total number of rentals
     */
    public int getTotalRentalCount() {
        return rentals.size();
    }
    
    /**
     * Reset rentals to initial state.
     */
    public void resetRentals() {
        rentals.clear();
        nextRentalId = 1001;
    }
}

