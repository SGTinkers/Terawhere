package tech.msociety.terawhere.models;

public class Booking {
    private String id;
    private String passengerId;
    private String numberOfSeats;
    private String meetUpTime;
    private String endingLocationName;


    private String driverName;

    public Booking(String driverName, String id, String passengerId, String numberOfSeats, String meetUpTime, String endingLocationName) {
        this.id = id;
        this.passengerId = passengerId;
        this.numberOfSeats = numberOfSeats;
        this.meetUpTime = meetUpTime;
        this.endingLocationName = endingLocationName;
        this.driverName = driverName;
    }

    public Booking(String driverName, String id, String passengerId, String numberOfSeats) {
        this.driverName = driverName;
        this.id = id;
        this.passengerId = passengerId;
        this.numberOfSeats = numberOfSeats;
    }

    public String getDriverName() {
        return driverName;
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
