package tech.msociety.terawhere.networkcalls.intereptors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import tech.msociety.terawhere.exceptions.NoConnectivityException;
import tech.msociety.terawhere.screens.activities.NoNetworkActivity;
import tech.msociety.terawhere.utils.NetworkUtils;

public class ConnectivityInterceptor implements Interceptor {
    private Context context;
    
    public ConnectivityInterceptor(Context context) {
        this.context = context;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkUtils.hasConnectivity(context)) {
            NoConnectivityException exception = new NoConnectivityException();
            Log.e("INTERCEPTOR", "ConnectivityInterceptor detects no connectivity", exception);
            Intent intent = new Intent(context, NoNetworkActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
        
        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
}
