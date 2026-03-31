package model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a rental transaction in the Vehicle Rental System.
 * Contains information about customer, vehicle, rental duration, and cost.
 * Rental is now based on date/time instead of days.
 */
public class Rental {
    private String rentalId;
    private String customerUsername;
    private String vehicleId;
    private String vehicleName;
    private double totalCost;
    private LocalDateTime rentalDate;       // Start date/time of rental
    private LocalDateTime expectedReturnDate; // Expected return date/time
    private LocalDateTime returnDate;
    private LocalDateTime giveBackDate; // Expected return date for lost items
    private String status; // "Active", "Returned", "Lost"
    
    public Rental(String rentalId, String customerUsername, String vehicleId, 
                  String vehicleName, double totalCost, LocalDateTime rentalDate, 
                  LocalDateTime expectedReturnDate) {
        this.rentalId = rentalId;
        this.customerUsername = customerUsername;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.totalCost = totalCost;
        this.rentalDate = rentalDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status = "Active";
    }
    
    // Getters and Setters
    public String getRentalId() {
        return rentalId;
    }
    
    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }
    
    public String getCustomerUsername() {
        return customerUsername;
    }
    
    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }
    
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
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    public LocalDateTime getRentalDate() {
        return rentalDate;
    }
    
    public void setRentalDate(LocalDateTime rentalDate) {
        this.rentalDate = rentalDate;
    }
    
    public LocalDateTime getExpectedReturnDate() {
        return expectedReturnDate;
    }
    
    public void setExpectedReturnDate(LocalDateTime expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }
    
    public LocalDateTime getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getGiveBackDate() {
        return giveBackDate;
    }

    public void setGiveBackDate(LocalDateTime giveBackDate) {
        this.giveBackDate = giveBackDate;
    }
    
    /**
     * Calculate the rental duration in hours.
     * @return Number of hours between rental date and expected return date
     */
    public long getRentalHours() {
        if (rentalDate != null && expectedReturnDate != null) {
            return ChronoUnit.HOURS.between(rentalDate, expectedReturnDate);
        }
        return 0;
    }
    
    /**
     * Calculate the rental duration as a formatted string.
     * @return Formatted duration string (e.g., "2h 30m")
     */
    public String getFormattedDuration() {
        if (rentalDate != null && expectedReturnDate != null) {
            long hours = ChronoUnit.HOURS.between(rentalDate, expectedReturnDate);
            long minutes = ChronoUnit.MINUTES.between(rentalDate, expectedReturnDate) % 60;
            return String.format("%dh %dm", hours, minutes);
        }
        return "0h 0m";
    }
    
    /**
     * Mark the rental as returned and set return date.
     */
    public void markAsReturned() {
        this.status = "Returned";
        this.returnDate = LocalDateTime.now();
    }

    /**
     * Mark the rental as lost and set expected give-back date.
     * @param giveBackDate Expected date when the lost item will be returned
     */
    public void markAsLost(LocalDateTime giveBackDate) {
        this.status = "Lost";
        this.giveBackDate = giveBackDate;
    }
}

