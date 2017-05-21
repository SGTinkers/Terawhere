package tech.msociety.terawhere.models.factories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.msociety.terawhere.models.BackendTimestamp;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.GetOffersResponse;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.OfferDatum;
import tech.msociety.terawhere.utils.DateUtils;

public class OfferFactory {
    public static List<Offer> createFromResponse(GetOffersResponse getOffersResponse) {
        List<Offer> offers = new ArrayList<>();
        Vehicle vehicle;
        Offer offer;
        for (OfferDatum offersDatum : getOffersResponse.data) {
            TerawhereLocation startTerawhereLocation = new TerawhereLocation(offersDatum.startName, offersDatum.startAddr, offersDatum.startLat, offersDatum.startLng, offersDatum.startGeohash);
            TerawhereLocation endTerawhereLocation = new TerawhereLocation(offersDatum.endName, offersDatum.endAddr, offersDatum.endLat, offersDatum.endLng, offersDatum.endGeohash);
            if (offersDatum.vehicleDesc == null) {
                vehicle = new Vehicle(offersDatum.vehicleNumber, "", offersDatum.vehicleModel);

            } else {
                vehicle = new Vehicle(offersDatum.vehicleNumber, offersDatum.vehicleDesc.toString(), offersDatum.vehicleModel);
            }
            Date dateCreated = DateUtils.fromMysqlDateTimeString(offersDatum.createdAt);
            Date dateUpdated = DateUtils.fromMysqlDateTimeString(offersDatum.updatedAt);
//            Date dateDeleted = DateUtils.fromMysqlDateTimeString(offersDatum.getDeletedAt());
            BackendTimestamp backendTimestamp = new BackendTimestamp(dateCreated, dateUpdated, null);
            Date meetupTime = DateUtils.fromMysqlDateTimeString(offersDatum.meetupTime);

            if (offersDatum.remarks == null) {
                offer = new Offer(offersDatum.id, offersDatum.userId, meetupTime, startTerawhereLocation, endTerawhereLocation, vehicle, offersDatum.vacancy, backendTimestamp, "", offersDatum.seatsRemaining, offersDatum.seatsBooked, offersDatum.driverName);

            } else {
                offer = new Offer(offersDatum.id, offersDatum.userId, meetupTime, startTerawhereLocation, endTerawhereLocation, vehicle, offersDatum.vacancy, backendTimestamp, offersDatum.remarks.toString(), offersDatum.seatsRemaining, offersDatum.seatsBooked, offersDatum.driverName);
            }
            offers.add(offer);
        }

        return offers;
    }
}
