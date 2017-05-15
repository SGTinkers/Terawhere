package tech.msociety.terawhere.networkcalls.intereptors;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import tech.msociety.terawhere.globals.Constants;

public class AuthorizationResponseInterceptor implements Interceptor {

    public AuthorizationResponseInterceptor() {
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.header("Authorization") != null && !response.header("Authorization").isEmpty()) {
            String authorization = response.header("Authorization");
            String[] authorizationSplit = authorization.split(" ");
            Constants.setBearerToken(authorizationSplit[1]);
            Log.d(AuthorizationResponseInterceptor.class.getName(), "Bearer token updated: " + Constants.getBearerToken());
        }

        return response;
    }
}