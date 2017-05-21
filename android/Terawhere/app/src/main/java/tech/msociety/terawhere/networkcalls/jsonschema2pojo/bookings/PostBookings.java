package tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by musa on 21/5/17.
 */

public class PostBookings {

    @SerializedName("offer_id")
    @Expose
    public Integer offerId;


    @SerializedName("pax")
    @Expose
    public Integer pax;

    public PostBookings(Integer offerId, Integer pax) {
        this.offerId = offerId;
        this.pax = pax;
    }

    public PostBookings(Integer offerId) {
        this.offerId = offerId;
    }



}
