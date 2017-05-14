package tech.msociety.terawhere.networkcalls.jsonschema2pojo.getuser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetUser {
    @SerializedName("user")
    @Expose
    private UserDatum user;

    @Override
    public String toString() {
        return "GetUser{" +
                "user=" + user.toString() +
                '}';
    }

    public UserDatum getUser() {
        return user;
    }
}
