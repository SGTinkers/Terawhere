package tech.msociety.terawhere.models.factories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.msociety.terawhere.models.BackendTimestamp;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.GetOffersResponse;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.OffersDatum;
import tech.msociety.terawhere.utils.DateUtils;

public class OfferFactory {
    public static List<Offer> createFromResponse(GetOffersResponse getOffersResponse) {
        List<Offer> offers = new ArrayList<>();
        
        for (OffersDatum offersDatum : getOffersResponse.data) {
            TerawhereLocation startTerawhereLocation = new TerawhereLocation(offersDatum.getStartName(), offersDatum.getStartAddr(), offersDatum.getStartLat(), offersDatum.getStartLng(), "geohash");
            TerawhereLocation endTerawhereLocation = new TerawhereLocation(offersDatum.getEndName(), offersDatum.getEndAddr(), offersDatum.getEndLat(), offersDatum.getEndLng(), "geohash");
            Vehicle vehicle = new Vehicle(offersDatum.getVehicleNumber(), offersDatum.getVehicleDesc(), offersDatum.getVehicleModel());
            
            Date dateCreated = DateUtils.fromMysqlDateTimeString(offersDatum.getCreatedAt());
            Date dateUpdated = DateUtils.fromMysqlDateTimeString(offersDatum.getUpdatedAt());
//            Date dateDeleted = DateUtils.fromMysqlDateTimeString(offersDatum.getDeletedAt());
            BackendTimestamp backendTimestamp = new BackendTimestamp(dateCreated, dateUpdated, null);
            Date meetupTime = DateUtils.fromMysqlDateTimeString(offersDatum.getMeetupTime());
            
            Offer offer = new Offer(offersDatum.getId(), offersDatum.getUserId(), meetupTime, startTerawhereLocation, endTerawhereLocation, vehicle, offersDatum.getVacancy(), backendTimestamp, offersDatum.getRemarks());
            offers.add(offer);
        }
        
        return offers;
    }
}
