package tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingRequestBody {

    @SerializedName("offer_id")
    @Expose
    private Integer offerId;

    @SerializedName("pax")
    @Expose
    private Integer pax;
    
    public BookingRequestBody(Integer offerId) {
        this.offerId = offerId;
    }
}
