package tech.msociety.terawhere;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("meetup_time")
    @Expose
    private String meetupTime;
    @SerializedName("start_name")
    @Expose
    private String startName;
    @SerializedName("start_addr")
    @Expose
    private String startAddr;
    @SerializedName("start_lat")
    @Expose
    private Double startLat;
    @SerializedName("start_lng")
    @Expose
    private Double startLng;
    @SerializedName("end_name")
    @Expose
    private String endName;
    @SerializedName("end_addr")
    @Expose
    private String endAddr;
    @SerializedName("end_lat")
    @Expose
    private Double endLat;
    @SerializedName("end_lng")
    @Expose
    private Double endLng;
    @SerializedName("vacancy")
    @Expose
    private Integer vacancy;
    @SerializedName("remarks")
    @Expose
    private Object remarks;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("pref_gender")
    @Expose
    private Object prefGender;
    @SerializedName("vehicle_number")
    @Expose
    private String vehicleNumber;
    @SerializedName("vehicle_desc")
    @Expose
    private Object vehicleDesc;
    @SerializedName("vehicle_model")
    @Expose
    private String vehicleModel;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMeetupTime() {
        return meetupTime;
    }

    public String getStartName() {
        return startName;
    }

    public String getStartAddr() {
        return startAddr;
    }

    public Double getStartLat() {
        return startLat;
    }

    public Double getStartLng() {
        return startLng;
    }

    public String getEndName() {
        return endName;
    }

    public String getEndAddr() {
        return endAddr;
    }

    public Double getEndLat() {
        return endLat;
    }

    public Double getEndLng() {
        return endLng;
    }

    public Integer getVacancy() {
        return vacancy;
    }

    public Object getRemarks() {
        return remarks;
    }

    public Integer getStatus() {
        return status;
    }

    public Object getPrefGender() {
        return prefGender;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public Object getVehicleDesc() {
        return vehicleDesc;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Datum{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", meetupTime='" + meetupTime + '\'' +
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
                ", status=" + status +
                ", prefGender=" + prefGender +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", vehicleDesc=" + vehicleDesc +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", deletedAt=" + deletedAt +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
