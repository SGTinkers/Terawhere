package tech.msociety.terawhere;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import tech.msociety.terawhere.events.TokenInvalidEvent;
import tech.msociety.terawhere.screens.activities.FacebookLoginActivity;

public class TerawhereApplication extends Application {

    public static Context ApplicationContext;

    public TerawhereApplication() {
        super();
        ApplicationContext = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenInvalidEvent(TokenInvalidEvent event) {
        Intent i = new Intent(this, FacebookLoginActivity.class);
        startActivity(i);
    }

}
