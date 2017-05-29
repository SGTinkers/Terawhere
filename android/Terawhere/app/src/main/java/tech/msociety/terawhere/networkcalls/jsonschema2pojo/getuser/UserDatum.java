package tech.msociety.terawhere.networkcalls.jsonschema2pojo.getuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDatum {
    @SerializedName("id")
    @Expose
    public String id;
    
    @SerializedName("name")
    @Expose
    public String name;
    
    @SerializedName("email")
    @Expose
    public String email;
    
    @SerializedName("dp")
    @Expose
    public String dp;

    @SerializedName("gender")
    @Expose
    public String gender;
}
