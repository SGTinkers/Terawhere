package tech.msociety.terawhere.intereptors;

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

    public LoggingInterceptor() {
        super();
        logger = Logger.DEFAULT;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        RequestBody requestBody = request.body();
        logger.log("--> " + URLDecoder.decode(request.url().url().toString(), "UTF-8"));

        Response response = chain.proceed(request);
        logger.log("<-- " + URLDecoder.decode(response.toString(), "UTF-8"));

        return response;
    }
}
