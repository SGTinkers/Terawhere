package tech.msociety.terawhere.models;

public class Booking {
    private String id;
    private String passengerId;
    private String numberOfSeats;
    private String meetUpTime;
    private String endingLocationName;

    public Booking(String id, String passengerId, String numberOfSeats, String meetUpTime, String endingLocationName) {
        this.id = id;
        this.passengerId = passengerId;
        this.numberOfSeats = numberOfSeats;
        this.meetUpTime = meetUpTime;
        this.endingLocationName = endingLocationName;
    }

    public Booking(String id, String passengerId, String numberOfSeats) {
        this.id = id;
        this.passengerId = passengerId;
        this.numberOfSeats = numberOfSeats;
    }


    public String getId() {
        return id;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getNumberOfSeats() {
        return numberOfSeats;
    }

    public String getMeetUpTime() {
        return meetUpTime;
    }

    public String getEndingLocationName() {
        return endingLocationName;
    }
}
