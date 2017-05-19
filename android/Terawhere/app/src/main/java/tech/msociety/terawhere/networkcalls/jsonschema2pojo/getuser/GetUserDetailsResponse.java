package tech.msociety.terawhere.networkcalls.jsonschema2pojo.getuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetUserDetailsResponse {
    @SerializedName("user")
    @Expose
    public UserDatum user;
}
