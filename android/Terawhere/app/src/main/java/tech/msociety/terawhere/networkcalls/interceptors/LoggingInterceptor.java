package tech.msociety.terawhere.networkcalls.interceptors;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor.Logger;
import okio.Buffer;

/**
 * A simpler logger that logs URL-decoded HttpUrl strings.
 */
public class LoggingInterceptor implements Interceptor {
    private final Logger logger;
    private StringBuilder stringBuilder;
    
    public LoggingInterceptor() {
        super();
        logger = Logger.DEFAULT;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();
        String requestBody = getRequestBodyAsString(request);
        String requestHeaders = request.headers().toString();
        String url = URLDecoder.decode(request.url().url().toString(), "UTF-8");
    
        stringBuilder = new StringBuilder();
        stringBuilder.append(method.toUpperCase());
        stringBuilder.append(" -> ");
        stringBuilder.append(url);
        stringBuilder.append("\n");
    
        if (requestBody != null) {
            stringBuilder.append("BODY");
            stringBuilder.append("\n");
            stringBuilder.append(requestBody);
            stringBuilder.append("\n");
        }
    
        if (!TextUtils.isEmpty(requestHeaders)) {
            stringBuilder.append("HEADERS");
            stringBuilder.append("\n");
            stringBuilder.append(requestHeaders);
            stringBuilder.append("\n");
        }
    
        logger.log(stringBuilder.toString());
    
        Response response = chain.proceed(request);
        int responseCode = response.code();
    
        if (responseCode != 200) {
            String responseMessage = response.message();
            stringBuilder = new StringBuilder();
        
            stringBuilder.append("URL: ");
            stringBuilder.append(url);
            stringBuilder.append("\n");
        
            stringBuilder.append("Code: ");
            stringBuilder.append(responseCode);
            stringBuilder.append("\n");
        
            stringBuilder.append("Message: ");
            stringBuilder.append(responseMessage);
            stringBuilder.append("\n");

            if (response.body() != null) {
                // Assume the error body won't be more than 160 bytes
                stringBuilder.append("Body: ");
                stringBuilder.append(response.peekBody(160).string());
                stringBuilder.append("\n");
            }
        
            Log.e("LoggingInterceptor", stringBuilder.toString());
        }
        logger.log("<- " + URLDecoder.decode(response.toString(), "UTF-8"));

        return response;
    }
    
    @Nullable
    private String getRequestBodyAsString(Request request) throws IOException {
        Request copy = request.newBuilder().build();
        RequestBody requestBody = copy.body();
        
        if (requestBody == null) return null;
        
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        
        return buffer.readUtf8();
    }
}
