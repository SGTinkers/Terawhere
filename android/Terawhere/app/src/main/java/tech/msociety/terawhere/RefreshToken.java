package tech.msociety.terawhere;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by musa on 9/5/17.
 */

public class RefreshToken {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
