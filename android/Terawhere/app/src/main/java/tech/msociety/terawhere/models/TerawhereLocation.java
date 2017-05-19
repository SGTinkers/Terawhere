package tech.msociety.terawhere.models;

public class TerawhereLocation {
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
}
