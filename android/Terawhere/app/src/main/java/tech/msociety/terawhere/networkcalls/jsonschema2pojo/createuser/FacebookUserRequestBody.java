package tech.msociety.terawhere.networkcalls.jsonschema2pojo.createuser;

import com.google.gson.annotations.SerializedName;

public class FacebookUserRequestBody {
    @SerializedName("token")
    private String token;
    
    @SerializedName("service")
    private String service;
    
    public FacebookUserRequestBody(String token, String service) {
        this.token = token;
        this.service = service;
    }
    
    public String getToken() {
        return token;
    }
}
