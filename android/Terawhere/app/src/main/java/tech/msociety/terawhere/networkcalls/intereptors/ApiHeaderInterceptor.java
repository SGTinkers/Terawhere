package tech.msociety.terawhere.networkcalls.intereptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiHeaderInterceptor implements Interceptor {
    private String token;
    
    public ApiHeaderInterceptor(String token) {
        this.token = token;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        if (request.url().encodedPath().contains("api")) {
            request = request.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer" + token)
                    .build();
        }
        
        return chain.proceed(request);
    }
}