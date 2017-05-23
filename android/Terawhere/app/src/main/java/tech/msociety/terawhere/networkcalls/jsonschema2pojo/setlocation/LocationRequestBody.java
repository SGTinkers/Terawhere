package tech.msociety.terawhere.networkcalls.jsonschema2pojo.setlocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationRequestBody {
    @SerializedName("lat")
    @Expose
    public Double lat;
    @SerializedName("lng")
    @Expose
    public Double lng;
    
    public LocationRequestBody(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
