package tech.msociety.terawhere.screens.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.globals.Constants;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.createuser.FacebookUser;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.abstracts.BaseActivity;

public class FacebookLoginActivity extends BaseActivity {
    public static final String MESSAGE_LOGGING_IN = "Logging in...";
    
    private CallbackManager callbackManager;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        doesNotRequireAuth = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        if (Constants.GetBearerToken() != null) {
            finish();
            return;
        }

        loginButton = (Button) findViewById(R.id.button_facebook_login);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FacebookLoginActivity", "onSuccess: " + loginResult.getAccessToken().getToken());
                TerawhereBackendServer.getAuthApiInstance().createUser(new FacebookUser(loginResult.getAccessToken().getToken(), "facebook")).enqueue(new Callback<FacebookUser>() {
                    @Override
                    public void onResponse(Call<FacebookUser> call, Response<FacebookUser> response) {
                        Log.d("FacebookLoginActivity", "Server Token: " + response.body().getToken());
                        Constants.SetBearerToken(response.body().getToken());
                        Toast.makeText(getApplicationContext(), MESSAGE_LOGGING_IN, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(FacebookLoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<FacebookUser> call, Throwable t) {
                        // TODO: Handle error
                        t.printStackTrace();
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.d("FacebookLoginActivity", "Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FacebookLoginActivity", "Error");
                error.printStackTrace();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(FacebookLoginActivity.this, Arrays.asList("public_profile"));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
