package tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfferDatum {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("meetup_time")
    @Expose
    public String meetupTime;
    @SerializedName("start_name")
    @Expose
    public String startName;
    @SerializedName("start_addr")
    @Expose
    public String startAddr;
    @SerializedName("start_lat")
    @Expose
    public Double startLat;
    @SerializedName("start_lng")
    @Expose
    public Double startLng;
    @SerializedName("start_geohash")
    @Expose
    public String startGeohash;
    @SerializedName("end_name")
    @Expose
    public String endName;
    @SerializedName("end_addr")
    @Expose
    public String endAddr;
    @SerializedName("end_lat")
    @Expose
    public Double endLat;
    @SerializedName("end_lng")
    @Expose
    public Double endLng;
    @SerializedName("end_geohash")
    @Expose
    public String endGeohash;
    @SerializedName("vacancy")
    @Expose
    public Integer vacancy;
    @SerializedName("remarks")
    @Expose
    public Object remarks;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("pref_gender")
    @Expose
    public Object prefGender;
    @SerializedName("vehicle_number")
    @Expose
    public String vehicleNumber;
    @SerializedName("vehicle_desc")
    @Expose
    public Object vehicleDesc;
    @SerializedName("vehicle_model")
    @Expose
    public String vehicleModel;
    @SerializedName("seats_booked")
    @Expose
    public Integer seatsBooked;
    @SerializedName("seats_remaining")
    @Expose
    public Integer seatsRemaining;
    @SerializedName("name")
    @Expose
    public String driverName;
    @SerializedName("deleted_at")
    @Expose
    public String deletedAt;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;


}
