package tech.msociety.terawhere.screens.activities.abstracts;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import tech.msociety.terawhere.globals.Constants;
import tech.msociety.terawhere.screens.activities.FacebookLoginActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();

    protected boolean skipCheckForLogin;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!skipCheckForLogin && Constants.GetBearerToken() == null) {
            Intent i = new Intent(this, FacebookLoginActivity.class);
            startActivity(i);
            finish();
        }
    }
}
