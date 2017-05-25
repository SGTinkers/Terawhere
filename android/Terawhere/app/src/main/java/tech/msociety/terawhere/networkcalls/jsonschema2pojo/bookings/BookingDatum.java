package tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getuser.UserDatum;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.offers.OfferDatum;

public class BookingDatum {
    
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("offer_id")
    @Expose
    public Integer offerId;
    
    @SerializedName("user_id")
    @Expose
    public String userId;
    
    @SerializedName("pax")
    @Expose
    public Integer pax;
    
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    
    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;

    @SerializedName("offer")
    @Expose
    public OfferDatum offer;
    
    @SerializedName("user")
    @Expose
    public UserDatum user;

}
