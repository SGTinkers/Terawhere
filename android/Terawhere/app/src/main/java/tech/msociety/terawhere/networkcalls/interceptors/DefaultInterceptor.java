package tech.msociety.terawhere.networkcalls.interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DefaultInterceptor implements Interceptor {

    public DefaultInterceptor() {
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        request = request.newBuilder()
            .addHeader("Accept", "application/json")
            .build();
        
        return chain.proceed(request);
    }
}