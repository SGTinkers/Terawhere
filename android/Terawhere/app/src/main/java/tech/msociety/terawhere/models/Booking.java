package tech.msociety.terawhere.models;

public class Booking {
    private String id;
    private String passengerId;
    private Integer numberOfSeats;
    private String timestamp;

    public Booking(String id, String passengerId, Integer numberOfSeats, String timestamp) {
        this.id = id;
        this.passengerId = passengerId;
        this.numberOfSeats = numberOfSeats;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
