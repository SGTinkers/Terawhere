package tech.msociety.terawhere.models;

import java.util.Date;

public class Offer {
    private Integer offerId;
    private String offererId;
    private Date meetupTime;
    private TerawhereLocation startTerawhereLocation;
    private TerawhereLocation endTerawhereLocation;
    private Vehicle vehicle;
    private Integer vacancy;
    private BackendTimestamp backendTimestamp;
    private String remarks;
    
    public Offer(Integer offerId, String offererId, Date meetupTime, TerawhereLocation startTerawhereLocation, TerawhereLocation endTerawhereLocation, Vehicle vehicle, Integer vacancy, BackendTimestamp backendTimestamp, String remarks) {
        this.offerId = offerId;
        this.offererId = offererId;
        this.meetupTime = meetupTime;
        this.startTerawhereLocation = startTerawhereLocation;
        this.endTerawhereLocation = endTerawhereLocation;
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
    
    public TerawhereLocation getStartTerawhereLocation() {
        return startTerawhereLocation;
    }
    
    public TerawhereLocation getEndTerawhereLocation() {
        return endTerawhereLocation;
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
