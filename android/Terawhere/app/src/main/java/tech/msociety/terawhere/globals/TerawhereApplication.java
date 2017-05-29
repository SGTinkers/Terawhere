package tech.msociety.terawhere.globals;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.events.LoginEvent;
import tech.msociety.terawhere.events.LogoutEvent;
import tech.msociety.terawhere.events.TokenInvalidEvent;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.storedevicetoken.DeviceTokenRequestBody;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.storedevicetoken.StoreDeviceToken;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.FacebookLoginActivity;

public class TerawhereApplication extends Application {

    public static Context ApplicationContext;

    private static String bearerToken;

    private MixpanelAPI mixpanel;

    public TerawhereApplication() {
        super();
        ApplicationContext = this;
    }

    public static String getBearerToken() {
        if (bearerToken == null) {
            bearerToken = AppPrefs.with(ApplicationContext).getBearerToken();
        }

        return bearerToken;
    }

    public static void setBearerToken(String token) {
        AppPrefs.with(ApplicationContext).setBearerToken(token);
        bearerToken = token;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        mixpanel = MixpanelAPI.getInstance(this, Constants.MIXPANEL_TOKEN);

        registerPushTokensWithBackend();
    }

    private void registerPushTokensWithBackend() {
        if (getBearerToken() != null) {
            final String token = FirebaseInstanceId.getInstance().getToken();
            if (token != null) {
                TerawhereBackendServer.getApiInstance().storeDeviceToken(new DeviceTokenRequestBody(token, "android")).enqueue(new Callback<StoreDeviceToken>() {
                    @Override
                    public void onResponse(Call<StoreDeviceToken> call, Response<StoreDeviceToken> response) {
                        Log.d(TerawhereApplication.class.getSimpleName(), "Submitted push token to server: " + token);
                    }

                    @Override
                    public void onFailure(Call<StoreDeviceToken> call, Throwable t) {
                        Log.d(TerawhereApplication.class.getSimpleName(), "Failed to submit push token: " + token);
                    }
                });
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBus.getDefault().unregister(this);
    }

    public void trackEvent(String event) {
        try {
            JSONObject props = new JSONObject();
            if (getBearerToken() != null) {
                props.put("Logged In", false);
            } else {
                props.put("User Id", AppPrefs.with(this).getUserId());
                props.put("User Name", AppPrefs.with(this).getUserName());
                props.put("Gender", AppPrefs.with(this).getUserName());
            }
            mixpanel.track(event, props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenInvalidEvent(TokenInvalidEvent event) {
        EventBus.getDefault().post(new LogoutEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEvent(LogoutEvent event) {
        setBearerToken(null);
        Intent i = new Intent(this, FacebookLoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        registerPushTokensWithBackend();
    }

}
