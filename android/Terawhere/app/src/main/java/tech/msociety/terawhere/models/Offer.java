package tech.msociety.terawhere.models;

import java.util.Date;

public class Offer {
    private String id;
    private String driverId;
    private String destination;
    private Integer numberOfSeats;
    private Date timestamp;
    private String remarks;

    public Offer(String id, String driverId, String destination, Integer numberOfSeats, Date timestamp, String remarks) {
        this.id = id;
        this.driverId = driverId;
        this.destination = destination;
        this.numberOfSeats = numberOfSeats;
        this.timestamp = timestamp;
        this.remarks = remarks;
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
}
