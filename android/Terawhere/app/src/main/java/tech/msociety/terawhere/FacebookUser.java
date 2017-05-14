package tech.msociety.terawhere;

import com.google.gson.annotations.SerializedName;

public class FacebookUser {
    @SerializedName("token")
    String mToken;

    @SerializedName("service")
    String mService;

    public FacebookUser(String token, String service) {
        this.mToken = token;
        this.mService = service;
    }

    @Override
    public String toString() {
        return "FacebookUser{" +
                "token=" + mToken +

                '}';
    }

    public String getToken() {
        return mToken;
    }
}
