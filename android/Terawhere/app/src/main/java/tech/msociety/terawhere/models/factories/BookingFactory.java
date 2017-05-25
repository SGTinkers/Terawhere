package tech.msociety.terawhere.models.factories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.msociety.terawhere.models.BackendTimestamp;
import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings.BookingDatum;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings.GetBookingsResponse;
import tech.msociety.terawhere.utils.DateUtils;

/**
 * Created by musa on 21/5/17.
 */

public class BookingFactory {
    public static List<Booking> createFromResponse(GetBookingsResponse getBookingsResponse) {
        List<Booking> bookings = new ArrayList<>();
        Vehicle vehicle;
        Booking booking;
        Offer offer;
        for (BookingDatum bookingDatum : getBookingsResponse.data) {
            TerawhereLocation startTerawhereLocation = new TerawhereLocation(bookingDatum.offer.startName, bookingDatum.offer.startAddr, bookingDatum.offer.startLat, bookingDatum.offer.startLng, bookingDatum.offer.startGeohash);
            TerawhereLocation endTerawhereLocation = new TerawhereLocation(bookingDatum.offer.endName, bookingDatum.offer.endAddr, bookingDatum.offer.endLat, bookingDatum.offer.endLng, bookingDatum.offer.endGeohash);
            if (bookingDatum.offer.vehicleDesc == null) {
                vehicle = new Vehicle(bookingDatum.offer.vehicleNumber, "", bookingDatum.offer.vehicleModel);

            } else {
                vehicle = new Vehicle(bookingDatum.offer.vehicleNumber, bookingDatum.offer.vehicleDesc.toString(), bookingDatum.offer.vehicleModel);
            }

            Date offerDateCreated = DateUtils.fromMysqlDateTimeString(bookingDatum.offer.createdAt);
            Date offerDateUpdated = DateUtils.fromMysqlDateTimeString(bookingDatum.offer.updatedAt);
            //            Date offerDateDeleted = DateUtils.fromMysqlDateTimeString(offersDatum.getDeletedAt());

            BackendTimestamp offerBackendTimestamp = new BackendTimestamp(offerDateCreated, offerDateUpdated, null);

            Date bookingDateCreated = DateUtils.fromMysqlDateTimeString(bookingDatum.createdAt);
            Date bookingDateUpdated = DateUtils.fromMysqlDateTimeString(bookingDatum.updatedAt);
            //            Date bookingDateDeleted = DateUtils.fromMysqlDateTimeString(offersDatum.getDeletedAt());
            BackendTimestamp bookingBackendTimestamp = new BackendTimestamp(bookingDateCreated, bookingDateUpdated, null);

            Date meetupTime = DateUtils.fromMysqlDateTimeString(bookingDatum.offer.meetupTime);

            if (bookingDatum.offer.remarks == null) {
                offer = new Offer(bookingDatum.offer.id, bookingDatum.offer.userId, meetupTime, startTerawhereLocation, endTerawhereLocation, vehicle, bookingDatum.offer.vacancy, offerBackendTimestamp, "");
            } else {
                offer = new Offer(bookingDatum.offer.id, bookingDatum.offer.userId, meetupTime, startTerawhereLocation, endTerawhereLocation, vehicle, bookingDatum.offer.vacancy, offerBackendTimestamp, bookingDatum.offer.remarks.toString());
            }
/*
            booking = new Booking(bookingDatum.id, bookingDatum.userId, bookingDatum.user.name, bookingDatum.user.dp, bookingDatum.user.email, bookingDatum.pax, bookingBackendTimestamp, offer);
*/
            booking = new Booking(bookingDatum.id, bookingDatum.userId, bookingDatum.pax, bookingBackendTimestamp, offer);

            bookings.add(booking);
        }

        return bookings;
    }
}






