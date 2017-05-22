package tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostBookings {

    @SerializedName("offer_id")
    @Expose
    private Integer offerId;

    @SerializedName("pax")
    @Expose
    private Integer pax;

    public PostBookings(Integer offerId, Integer pax) {
        this.offerId = offerId;
        this.pax = pax;
    }

    public PostBookings(Integer offerId) {
        this.offerId = offerId;
    }



}
