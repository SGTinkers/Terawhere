package tech.msociety.terawhere.networkcalls.jsonschema2pojo.storedevicetoken;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceTokenDatum {
    @SerializedName("device_token")
    @Expose
    String deviceToken;

    @SerializedName("platform")
    @Expose
    String platform;

    public DeviceTokenDatum(String deviceToken, String platform) {
        this.deviceToken = deviceToken;
        this.platform = platform;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return "DeviceTokenDatum{" +
                "deviceToken='" + deviceToken + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }

}
