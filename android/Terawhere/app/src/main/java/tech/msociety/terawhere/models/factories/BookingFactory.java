package tech.msociety.terawhere.models.factories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.msociety.terawhere.models.BackendTimestamp;
import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings.BookingDatum;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings.GetBookingsResponse;
import tech.msociety.terawhere.utils.DateUtils;

public class BookingFactory {
    public static List<Booking> createFromResponse(GetBookingsResponse getBookingsResponse) {
        List<Booking> bookings = new ArrayList<>();
        Booking booking;
        for (BookingDatum bookingDatum : getBookingsResponse.data) {
            Date bookingDateCreated = DateUtils.mysqlDateTimeStringToDate(bookingDatum.createdAt);
            Date bookingDateUpdated = DateUtils.mysqlDateTimeStringToDate(bookingDatum.updatedAt);
            //            Date bookingDateDeleted = DateUtils.fromMysqlDateTimeString(offersDatum.getDeletedAt());
            BackendTimestamp bookingBackendTimestamp = new BackendTimestamp(bookingDateCreated, bookingDateUpdated, null);
    
            Offer offer = OfferFactory.createFromDatum(bookingDatum.offer);
            booking = new Booking(bookingDatum.id, bookingDatum.userId, bookingDatum.pax, bookingBackendTimestamp, offer);
            bookings.add(booking);
        }
    
        return bookings;
    }
}






