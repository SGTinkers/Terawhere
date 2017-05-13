package tech.msociety.terawhere;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by musa on 12/5/17.
 */

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
