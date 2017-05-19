package tech.msociety.terawhere.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TerawhereLocation implements Parcelable {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String geohash;
    
    public TerawhereLocation(String name, String address, Double latitude, Double longitude, String geohash) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geohash = geohash;
    }

    /**
     * Retrieving TerawhereLocation data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    protected TerawhereLocation(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.geohash = in.readString();
    }

    public static final Creator<TerawhereLocation> CREATOR = new Creator<TerawhereLocation>() {
        @Override
        public TerawhereLocation createFromParcel(Parcel in) {
            return new TerawhereLocation(in);
        }

        @Override
        public TerawhereLocation[] newArray(int size) {
            return new TerawhereLocation[size];
        }
    };

    public String getName() {
        return name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public String getGeohash() {
        return geohash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Storing the TerawhereLocation data to Parcel object
     **/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(geohash);
    }
}
