package tech.msociety.terawhere.models;

public class Offer {
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
}
