package tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.msociety.terawhere.models.BackendTimestamp;
import tech.msociety.terawhere.models.Location;
import tech.msociety.terawhere.models.OfferRevamp;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.utils.DateUtils;

public class GetOffersResponse {
    @SerializedName("data")
    @Expose
    private List<OffersDatum> data = null;
    
    @Override
    public String toString() {
        return "GetOffersResponse{" +
                "data=" + data +
                '}';
    }
    
    public GetOffersResponse(OffersDatum offer) {
        data = new ArrayList<>();
        data.add(offer);
        
    }
    
    public List<OfferRevamp> getOffers() {
        List<OfferRevamp> offers = new ArrayList<>();
        
        for (OffersDatum datum : data) {
            Location startLocation = new Location(datum.getStartName(), datum.getStartAddr(), datum.getStartLat(), datum.getStartLng(), "geohash");
            Location endLocation = new Location(datum.getEndName(), datum.getEndAddr(), datum.getEndLat(), datum.getEndLng(), "geohash");
            Vehicle vehicle = new Vehicle(datum.getVehicleNumber(), datum.getVehicleDesc(), datum.getVehicleModel());
    
            Date dateCreated = DateUtils.fromMysqlDateTimeString(datum.getCreatedAt());
            Date dateUpdated = DateUtils.fromMysqlDateTimeString(datum.getUpdatedAt());
//            Date dateDeleted = DateUtils.fromMysqlDateTimeString(datum.getDeletedAt());
            BackendTimestamp backendTimestamp = new BackendTimestamp(dateCreated, dateUpdated, null);
            Date meetupTime = DateUtils.fromMysqlDateTimeString(datum.getMeetupTime());
    
            OfferRevamp offer = new OfferRevamp(datum.getId(), datum.getUserId(), meetupTime, startLocation, endLocation, vehicle, datum.getVacancy(), backendTimestamp, datum.getRemarks());
            offers.add(offer);
        }
        
        return offers;
    }
}
