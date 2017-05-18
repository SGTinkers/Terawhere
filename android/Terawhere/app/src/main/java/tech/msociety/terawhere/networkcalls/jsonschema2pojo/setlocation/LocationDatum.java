package tech.msociety.terawhere.networkcalls.jsonschema2pojo.setlocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by musa on 18/5/17.
 */

public class LocationDatum {

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }


    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;


    public LocationDatum(Double lat, Double lng, Double range) {
        this.lat = lat;
        this.lng = lng;
    }

    public LocationDatum(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "LocationDatum{" +
                "lat=" + lat +
                ", lng=" + lng +

                '}';
    }


}
