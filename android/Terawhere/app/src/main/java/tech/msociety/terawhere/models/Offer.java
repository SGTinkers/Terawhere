package tech.msociety.terawhere.models;

import java.util.Date;

public class Offer {
    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", driverId='" + driverId + '\'' +
                ", meetUpTime='" + meetUpTime + '\'' +
                ", startingLocationName='" + startingLocationName + '\'' +
                ", startingLocationAddress='" + startingLocationAddress + '\'' +
                ", startingLocationLatitude=" + startingLocationLatitude +
                ", startingLocationLongitude=" + startingLocationLongitude +
                ", endingLocationName='" + endingLocationName + '\'' +
                ", endingLocationAddress='" + endingLocationAddress + '\'' +
                ", endingLocationLatitude=" + endingLocationLatitude +
                ", endingLocationLongitude=" + endingLocationLongitude +
                ", seatsAvailable=" + seatsAvailable +
                ", driverRemarks='" + driverRemarks + '\'' +
                ", offerStatus=" + offerStatus +
                ", genderPreference='" + genderPreference + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", vehicleDescription='" + vehicleDescription + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                '}';
    }

    private int id;
    private String driverId;
    private String meetUpTime;

    private String startingLocationName;
    private String startingLocationAddress;
    private Double startingLocationLatitude;
    private Double startingLocationLongitude;

    private String endingLocationName;
    private String endingLocationAddress;
    private Double endingLocationLatitude;
    private Double endingLocationLongitude;

    private int seatsAvailable;
    private String driverRemarks;

    private int offerStatus;
    private String genderPreference;
    private String vehicleNumber;
    private String vehicleDescription;
    private String vehicleModel;

    public Offer(String driverId, Date meetUpTime,
                 String startingLocationName, String startingLocationAddress,
                 Double startingLocationLatitude, Double startingLocationLongitude,
                 String endingLocationName, String endingLocationAddress,
                 Double endingLocationLatitude, Double endingLocationLongitude,
                 int seatsAvailable, String driverRemarks, int offerStatus, String genderPreference, String vehicleNumber,
                 String vehicleDescription, String vehicleModel) {

        this.driverId = driverId;
        this.meetUpTime = meetUpTime.toString();
        this.startingLocationName = startingLocationName;
        this.startingLocationAddress = startingLocationAddress;
        this.startingLocationLatitude = startingLocationLatitude;
        this.startingLocationLongitude = startingLocationLongitude;
        this.endingLocationName = endingLocationName;
        this.endingLocationAddress = endingLocationAddress;
        this.endingLocationLatitude = endingLocationLatitude;
        this.endingLocationLongitude = endingLocationLongitude;
        this.seatsAvailable = seatsAvailable;
        this.driverRemarks = driverRemarks;
        this.offerStatus = offerStatus;
        this.genderPreference = genderPreference;
        this.vehicleNumber = vehicleNumber;
        this.vehicleDescription = vehicleDescription;
        this.vehicleModel = vehicleModel;

    }

    public Offer(int id, String driverId, String destination, Integer numberOfSeats, Date timestamp, String remarks, String vehicleColor, String vehiclePlateNumber) {
        this.id = id;
        this.driverId = driverId;
        this.endingLocationName = destination;
        this.seatsAvailable = numberOfSeats;
        this.meetUpTime = timestamp.toString();
        this.driverRemarks = remarks;
        this.vehicleDescription = vehicleColor;
        this.vehicleNumber = vehiclePlateNumber;
    }

    public Offer(String userId, String remarks, String vehicleDesc, String vehicleNumber) {
        this.driverId = userId;
        this.driverRemarks = remarks;
        this.vehicleDescription = vehicleDesc;
        this.vehicleNumber = vehicleNumber;
    }

    public Offer(int id, String userId, String meetupTime, String startAddr, String startName, double startingLocationLatitude, double startingLocationLongitude, String endName, String endAddr, double endingLocationLatitude, double endingLocationLongitude, int vacancy, String vehicleModel, String vehicleNumber, String remarks, String genderPreference, String vehicleDesc) {
        this.id = id;
        this.driverId = userId;
        this.meetUpTime = meetupTime;
        this.startingLocationAddress = startAddr;
        this.startingLocationName = startName;
        this.endingLocationName = endName;
        this.endingLocationAddress = endAddr;
        this.seatsAvailable = vacancy;
        this.vehicleModel = vehicleModel;
        this.vehicleNumber = vehicleNumber;
        this.driverRemarks = remarks;
        this.genderPreference = genderPreference;
        this.vehicleDescription = vehicleDesc;
        this.startingLocationLatitude = startingLocationLatitude;
        this.startingLocationLongitude = startingLocationLongitude;
        this.endingLocationLatitude = endingLocationLatitude;
        this.endingLocationLongitude = endingLocationLongitude;
    }

    public int getId() {
        return id;
    }
    public String getDriverId() {
        return driverId;
    }

    public String getMeetUpTime() {
        return meetUpTime;
    }

    public String getStartingLocationName() {
        return startingLocationName;
    }

    public String getStartingLocationAddress() {
        return startingLocationAddress;
    }

    public Double getStartingLocationLatitude() {
        return startingLocationLatitude;
    }

    public Double getStartingLocationLongitude() {
        return startingLocationLongitude;
    }

    public String getEndingLocationName() {
        return endingLocationName;
    }

    public String getEndingLocationAddress() {
        return endingLocationAddress;
    }

    public Double getEndingLocationLatitude() {
        return endingLocationLatitude;
    }

    public Double getEndingLocationLongitude() {
        return endingLocationLongitude;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public String getDriverRemarks() {
        return driverRemarks;
    }

    public int getOfferStatus() {
        return offerStatus;
    }

    public String getGenderPreference() {
        return genderPreference;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

}
