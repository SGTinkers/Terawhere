package tech.msociety.terawhere.models;

import java.util.Date;

public class Offer {
    private final String id;
    private String driverId;
    private final String destination;
    private Integer numberOfSeats;
    private Date timestamp;
    private String remarks;
    private String vehicleColor;
    private String vehiclePlateNumber;

    public Offer(String id, String driverId, String destination, Integer numberOfSeats, Date timestamp, String remarks, String vehicleColor, String vehiclePlateNumber) {
        this.id = id;
        this.driverId = driverId;
        this.destination = destination;
        this.numberOfSeats = numberOfSeats;
        this.timestamp = timestamp;
        this.remarks = remarks;
        this.vehicleColor = vehicleColor;
        this.vehiclePlateNumber = vehiclePlateNumber;
    }

    public Offer(String id, String endName) {
        this.id = id;
        destination = endName;
    }

    public String getId() {
        return id;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getDestination() {
        return destination;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public String getVehiclePlateNumber() {
        return vehiclePlateNumber;
    }
}
