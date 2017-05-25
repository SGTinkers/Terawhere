package tech.msociety.terawhere.models.factories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.msociety.terawhere.models.BackendTimestamp;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.offers.GetOffersResponse;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.offers.OfferDatum;
import tech.msociety.terawhere.utils.DateUtils;

public class OfferFactory {
    public static List<Offer> createFromResponse(GetOffersResponse getOffersResponse) {
        List<Offer> offers = new ArrayList<>();

        for (OfferDatum offerDatum : getOffersResponse.data) {
            Offer offer = createFromDatum(offerDatum);
            offers.add(offer);
        }

        return offers;
    }

    public static Offer createFromDatum(OfferDatum offerDatum) {
        TerawhereLocation startTerawhereLocation = new TerawhereLocation(offerDatum.startName, offerDatum.startAddr, offerDatum.startLat, offerDatum.startLng, offerDatum.startGeohash);
        TerawhereLocation endTerawhereLocation = new TerawhereLocation(offerDatum.endName, offerDatum.endAddr, offerDatum.endLat, offerDatum.endLng, offerDatum.endGeohash);
        Vehicle vehicle = new Vehicle(offerDatum.vehicleNumber, offerDatum.vehicleDesc, offerDatum.vehicleModel);
        Date dateCreated = DateUtils.fromMysqlDateTimeString(offerDatum.createdAt);
        Date dateUpdated = DateUtils.fromMysqlDateTimeString(offerDatum.updatedAt);
//            Date dateDeleted = DateUtils.fromMysqlDateTimeString(offersDatum.getDeletedAt());
        BackendTimestamp backendTimestamp = new BackendTimestamp(dateCreated, dateUpdated, null);
        Date meetupTime = DateUtils.fromMysqlDateTimeString(offerDatum.meetupTime);

        Offer offer = new Offer(offerDatum.id, offerDatum.userId, offerDatum.user.name, meetupTime, startTerawhereLocation, endTerawhereLocation, vehicle, offerDatum.vacancy, offerDatum.seatsBooked, backendTimestamp);
        offer.setRemarks(offerDatum.remarks);
        offer.setOffererDp(offerDatum.user.dp);

        return offer;
    }
}
