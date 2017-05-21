package tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by musa on 13/5/17.
 */

public class BookingDatum {
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getUserId() {
        return userId;
    }


    public String getPax() {
        return pax;
    }

    public void setPax(String pax) {
        this.pax = pax;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BookingDatum{" +
                "offerId='" + offerId + '\'' +
                ", userId='" + userId + '\'' +
                "driverName='" + driverName + '\'' +
                ", pax='" + pax + '\'' +
                ", id='" + id + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    public BookingDatum(String driverName, String offerId, String userId, String pax) {
        this.offerId = offerId;
        this.userId = userId;
        this.pax = pax;
        this.driverName = driverName;
    }

    public BookingDatum(String offerId, String userId, String pax) {
        this.offerId = offerId;
        this.userId = userId;
        this.pax = pax;
        this.driverName = driverName;
    }

    @SerializedName("offer_id")
    @Expose
    private String offerId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("pax")
    @Expose
    private String pax;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public String getDriverName() {
        return driverName;
    }

    @SerializedName("name")
    @Expose
    private String driverName;


}
