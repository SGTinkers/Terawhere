package tech.msociety.terawhere.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.globals.Constants;
import tech.msociety.terawhere.globals.TerawhereApplication;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.storedevicetoken.DeviceTokenRequestBody;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.storedevicetoken.StoreDeviceToken;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(FirebaseInstanceIdService.class.getSimpleName(), "Refreshed token: " + refreshedToken);

        if (TerawhereApplication.getBearerToken() != null) {
            TerawhereBackendServer.getApiInstance().storeDeviceToken(new DeviceTokenRequestBody(refreshedToken, "android")).enqueue(new Callback<StoreDeviceToken>() {
                @Override
                public void onResponse(Call<StoreDeviceToken> call, Response<StoreDeviceToken> response) {
                    Log.d(FirebaseInstanceIdService.class.getSimpleName(), "Submitted push token to server: " + refreshedToken);
                }

                @Override
                public void onFailure(Call<StoreDeviceToken> call, Throwable t) {
                    Log.d(FirebaseInstanceIdService.class.getSimpleName(), "Failed to submit push token: " + refreshedToken);
                }
            });
        }
    }

}
