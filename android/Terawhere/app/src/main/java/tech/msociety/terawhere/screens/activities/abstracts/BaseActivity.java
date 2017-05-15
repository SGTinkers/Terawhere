package tech.msociety.terawhere.screens.activities.abstracts;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import tech.msociety.terawhere.events.LogoutEvent;
import tech.msociety.terawhere.events.TokenInvalidEvent;
import tech.msociety.terawhere.globals.Constants;
import tech.msociety.terawhere.screens.activities.FacebookLoginActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();

    protected boolean doesNotRequireAuth;

    protected boolean registerEventBus;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (registerEventBus) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!doesNotRequireAuth && Constants.GetBearerToken() == null) {
            Intent i = new Intent(this, FacebookLoginActivity.class);
            startActivity(i);
            finish();
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
        if (!doesNotRequireAuth) {
            finish();
        }
    }
}
