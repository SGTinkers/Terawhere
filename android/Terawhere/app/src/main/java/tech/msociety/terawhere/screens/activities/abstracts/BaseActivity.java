package tech.msociety.terawhere.screens.activities.abstracts;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.events.LogoutEvent;
import tech.msociety.terawhere.globals.Constants;
import tech.msociety.terawhere.screens.activities.FacebookLoginActivity;
import tech.msociety.terawhere.screens.activities.NoNetworkActivity;
import tech.msociety.terawhere.screens.activities.RequestLocationServicesActivity;
import tech.msociety.terawhere.utils.NetworkUtils;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    
    protected boolean requireAuth = true;
    
    protected boolean requireLocationServices = true;
    
    protected boolean requireNetwork = true;
    
    protected boolean registerEventBus = true;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    
        if (registerEventBus) {
            EventBus.getDefault().register(this);
        }
        
        if (requireAuth && Constants.getBearerToken() == null) {
            Intent i = new Intent(this, FacebookLoginActivity.class);
            startActivity(i);
            finish();
            return;
        }
    
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (requireLocationServices && permissionCheck == PackageManager.PERMISSION_DENIED) {
            Intent i = new Intent(this, RequestLocationServicesActivity.class);
            startActivity(i);
            finish();
            return;
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // Set the color of Terawhere title on Recent Apps
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_terawhere_logo);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.colorPrimaryDark));
            setTaskDescription(taskDesc);
        }

        if (requireNetwork) {
            if (!NetworkUtils.hasConnectivity(this)) {
                startActivity(new Intent(this, NoNetworkActivity.class));
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (registerEventBus) {
            EventBus.getDefault().unregister(this);
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEvent(LogoutEvent event) {
        if (requireAuth) {
            finish();
        }
    }
}
