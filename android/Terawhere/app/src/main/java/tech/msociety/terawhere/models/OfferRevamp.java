package tech.msociety.terawhere.models;

import java.util.Date;

public class OfferRevamp {
    private Integer offerId;
    private String offererId;
    private Date meetupTime;
    private Location startLocation;
    private Location endLocation;
    private Vehicle vehicle;
    private Integer vacancy;
    private BackendTimestamp backendTimestamp;
    private String remarks;
    
    public OfferRevamp(Integer offerId, String offererId, Date meetupTime, Location startLocation, Location endLocation, Vehicle vehicle, Integer vacancy, BackendTimestamp backendTimestamp, String remarks) {
        this.offerId = offerId;
        this.offererId = offererId;
        this.meetupTime = meetupTime;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.vehicle = vehicle;
        this.vacancy = vacancy;
        this.backendTimestamp = backendTimestamp;
        this.remarks = remarks;
    }
    
    public Integer getOfferId() {
        return offerId;
    }
    
    public String getOffererId() {
        return offererId;
    }
    
    public Date getMeetupTime() {
        return meetupTime;
    }
    
    public Location getStartLocation() {
        return startLocation;
    }
    
    public Location getEndLocation() {
        return endLocation;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public Integer getVacancy() {
        return vacancy;
    }
    
    public BackendTimestamp getBackendTimestamp() {
        return backendTimestamp;
    }
    
    public String getRemarks() {
        return remarks;
    }
}
