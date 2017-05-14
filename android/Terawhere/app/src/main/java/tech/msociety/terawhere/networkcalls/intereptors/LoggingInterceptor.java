package tech.msociety.terawhere.networkcalls.intereptors;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor.Logger;

/**
 * A simpler logger that logs URL-decoded HttpUrl strings.
 */
public class LoggingInterceptor implements Interceptor {
    private final Logger logger;
    private String token;

    public LoggingInterceptor(String token) {
        super();
        this.token = token;
        logger = Logger.DEFAULT;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request.newBuilder().addHeader("Authorization", "Bearer " + token);
        request.newBuilder().addHeader("Accept", "application/json");
        request.newBuilder().addHeader("Content-Type", "application/x-www-form-urlencoded");

        RequestBody requestBody = request.body();
        logger.log("--> " + URLDecoder.decode(request.url().url().toString(), "UTF-8"));

        Response response = chain.proceed(request);
        logger.log("<-- " + URLDecoder.decode(response.toString(), "UTF-8"));

        return response;
    }


}
