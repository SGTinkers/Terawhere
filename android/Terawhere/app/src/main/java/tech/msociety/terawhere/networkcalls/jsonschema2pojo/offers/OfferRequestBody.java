package tech.msociety.terawhere.networkcalls.jsonschema2pojo.offers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfferRequestBody {
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
    
    @SerializedName("vacancy")
    @Expose
    public Integer vacancy;
    
    @SerializedName("remarks")
    @Expose
    public Object remarks;
    
    @SerializedName("vehicle_number")
    @Expose
    public String vehicleNumber;
    
    @SerializedName("vehicle_desc")
    @Expose
    public Object vehicleDesc;
    
    @SerializedName("vehicle_model")
    @Expose
    public String vehicleModel;
    
    public OfferRequestBody(String meetupTime,
                            String startName,
                            String startAddr,
                            Double startLat,
                            Double startLng,
                            String endName,
                            String endAddr,
                            Double endLat,
                            Double endLng,
                            Integer vacancy,
                            Object remarks,
                            String vehicleNumber,
                            Object vehicleDesc,
                            String vehicleModel) {
        this.meetupTime = meetupTime;
        this.startName = startName;
        this.startAddr = startAddr;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endName = endName;
        this.endAddr = endAddr;
        this.endLat = endLat;
        this.endLng = endLng;
        this.vacancy = vacancy;
        this.remarks = remarks;
        this.vehicleNumber = vehicleNumber;
        this.vehicleDesc = vehicleDesc;
        this.vehicleModel = vehicleModel;
    }
    
    @Override
    public String toString() {
        return "OfferRequestBody{" +
                "meetupTime='" + meetupTime + '\'' +
                ", startName='" + startName + '\'' +
                ", startAddr='" + startAddr + '\'' +
                ", startLat=" + startLat +
                ", startLng=" + startLng +
                ", endName='" + endName + '\'' +
                ", endAddr='" + endAddr + '\'' +
                ", endLat=" + endLat +
                ", endLng=" + endLng +
                ", vacancy=" + vacancy +
                ", remarks=" + remarks +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", vehicleDesc=" + vehicleDesc +
                ", vehicleModel='" + vehicleModel + '\'' +
                '}';
    }
}
