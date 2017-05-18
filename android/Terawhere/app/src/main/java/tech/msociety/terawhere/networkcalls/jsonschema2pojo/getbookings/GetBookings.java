package tech.msociety.terawhere.networkcalls.jsonschema2pojo.getbookings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import tech.msociety.terawhere.models.Booking;

/**
 * Created by musa on 13/5/17.
 */

public class GetBookings {

    @SerializedName("data")
    @Expose
    private List<BookingDatum> data = null;

    @Override
    public String toString() {
        return "GetBookings{" +
                "data=" + data +
                '}';
    }

    public GetBookings(BookingDatum booking) {
        data = new ArrayList<>();
        data.add(booking);

    }

    public List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();

        for (BookingDatum datum : data) {
            Booking booking = new Booking(datum.getDriverName(), datum.getOfferId(), datum.getUserId(), datum.getPax());
            bookings.add(booking);
        }

        return bookings;
    }
}
