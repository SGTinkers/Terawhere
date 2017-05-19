package tech.msociety.terawhere.networkcalls.intereptors;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import tech.msociety.terawhere.events.LoginEvent;
import tech.msociety.terawhere.events.TokenInvalidEvent;
import tech.msociety.terawhere.globals.Constants;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;

public class AuthorizationRequestInterceptor implements Interceptor {

    private boolean handleTokenExpiry;

    public AuthorizationRequestInterceptor() {
        handleTokenExpiry = true;
    }

    public AuthorizationRequestInterceptor(boolean handleTokenExpiry) {
        this.handleTokenExpiry = handleTokenExpiry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (Constants.getBearerToken() != null) {
            request = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + Constants.getBearerToken())
                    .build();
        }

        Response response = chain.proceed(request);

        if (handleTokenExpiry && !response.isSuccessful()) {
            try {
                Error e = new Gson().fromJson(response.body().string(), Error.class);
                if (e != null && e.getError() != null && e.getError().equals("token_expired")) {
                    retrofit2.Response<Void> responseRefreshToken = TerawhereBackendServer.getAuthApiInstance().refreshToken().execute();
                    if (responseRefreshToken.isSuccessful()) {
                        EventBus.getDefault().post(new LoginEvent());
                        return intercept(chain);
                    } else {
                        throw new TokenInvalidException();
                    }
                } else if (e != null && e.getError() != null && e.getError().equals("token_invalid")) {
                    EventBus.getDefault().post(new TokenInvalidEvent());
                    throw new TokenInvalidException();
                } else if (e != null && e.getError() != null && e.getError().equals("user_not_found")) {
                    EventBus.getDefault().post(new TokenInvalidEvent());
                    throw new TokenInvalidException();
                }
            } catch (JsonParseException e) {
            }
        }

        return response;
    }

    private class Error {

        @SerializedName("error")
        @Expose
        String error;

        public String getError() {
            return error;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "error='" + error + '\'' +
                    '}';
        }

    }

    public static class TokenInvalidException extends IOException {

    }
}