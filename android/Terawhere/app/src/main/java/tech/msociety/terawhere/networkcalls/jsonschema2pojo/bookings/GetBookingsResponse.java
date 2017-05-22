package tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBookingsResponse {
    @SerializedName("data")
    @Expose
    public List<BookingDatum> data = null;
}
