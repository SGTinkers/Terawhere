package tech.msociety.terawhere.networkcalls.jsonschema2pojo.storedevicetoken;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceTokenRequestBody {
    @SerializedName("device_token")
    @Expose
    private String deviceToken;

    @SerializedName("platform")
    @Expose
    private String platform;
    
    public DeviceTokenRequestBody(String deviceToken, String platform) {
        this.deviceToken = deviceToken;
        this.platform = platform;
    }
}
