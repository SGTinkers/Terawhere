package tech.msociety.terawhere.screens.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.crash.FirebaseCrash;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.events.LoginEvent;
import tech.msociety.terawhere.globals.AppPrefs;
import tech.msociety.terawhere.globals.Constants;
import tech.msociety.terawhere.globals.TerawhereApplication;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.createuser.FacebookUserRequestBody;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getuser.GetUserDetailsResponse;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.abstracts.BaseActivity;

public class FacebookLoginActivity extends BaseActivity {
    private CallbackManager callbackManager;
    private ImageButton loginButton;

    {
        requireAuth = false;
        requireLocationServices = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        if (TerawhereApplication.getBearerToken() != null) {
            finish();
            return;
        }

        loginButton = (ImageButton) findViewById(R.id.button_facebook_login);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FacebookLoginActivity", "onSuccess: " + loginResult.getAccessToken().getToken());
                Toast.makeText(FacebookLoginActivity.this, R.string.please_wait, Toast.LENGTH_SHORT).show();
                TerawhereBackendServer.getAuthApiInstance().createUser(new FacebookUserRequestBody(loginResult.getAccessToken().getToken(), "facebook")).enqueue(new Callback<FacebookUserRequestBody>() {
                    @Override
                    public void onResponse(Call<FacebookUserRequestBody> call, Response<FacebookUserRequestBody> response) {
                        Log.d("FacebookLoginActivity", "Server Token: " + response.body().getToken());
                        TerawhereApplication.setBearerToken(response.body().getToken());

                        EventBus.getDefault().post(new LoginEvent());
                        getUserDetails();
                    }

                    @Override
                    public void onFailure(Call<FacebookUserRequestBody> call, Throwable t) {
                        // TODO: Handle error more elegantly
                        FirebaseCrash.report(t);
                        t.printStackTrace();
                        Toast.makeText(FacebookLoginActivity.this, R.string.error_login_fail, Toast.LENGTH_SHORT).show();
                        loginButton.setEnabled(true);
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.d("FacebookLoginActivity", "Cancel");
                loginButton.setEnabled(true);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FacebookLoginActivity", "Error");
                FirebaseCrash.report(error);
                error.printStackTrace();
                Toast.makeText(FacebookLoginActivity.this, R.string.error_login_fail_fb, Toast.LENGTH_SHORT).show();
                loginButton.setEnabled(true);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(FacebookLoginActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
                loginButton.setEnabled(false);
            }
        });

        findViewById(R.id.text_view_terms_and_conditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://terawhere.com/terms.pdf"));
                startActivity(intent);
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    
    private void getUserDetails() {
        TerawhereBackendServer.getApiInstance().getStatus().enqueue(new Callback<GetUserDetailsResponse>() {
            @Override
            public void onResponse(Call<GetUserDetailsResponse> call, Response<GetUserDetailsResponse> response) {
                if (response.isSuccessful()) {
                    GetUserDetailsResponse getUserDetailsResponse = response.body();
                    
                    AppPrefs.with(TerawhereApplication.ApplicationContext).setUserId(getUserDetailsResponse.user.id);
                    AppPrefs.with(TerawhereApplication.ApplicationContext).setUserName(getUserDetailsResponse.user.name);
                    AppPrefs.with(TerawhereApplication.ApplicationContext).setUserEmail(getUserDetailsResponse.user.email);
                    AppPrefs.with(TerawhereApplication.ApplicationContext).setUserGender(getUserDetailsResponse.user.gender);

                    ((TerawhereApplication) getApplication()).trackEvent("User logged in");

                    try {
                        JSONObject props = new JSONObject();
                        props.put("$name", getUserDetailsResponse.user.name);
                        props.put("Name", getUserDetailsResponse.user.name);
                        props.put("Gender", getUserDetailsResponse.user.gender);
                        ((TerawhereApplication) getApplication()).getMixpanel().identify(getUserDetailsResponse.user.id);
                        ((TerawhereApplication) getApplication()).getMixpanel().getPeople().identify(getUserDetailsResponse.user.id);
                        ((TerawhereApplication) getApplication()).getMixpanel().getPeople().set(props);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ((TerawhereApplication) getApplication()).registerPushTokensWithBackend();
                    
                    goToMainActivity();
                } else {
                    Log.e("SAIFUL", "network call not successful");
                }
            }
            
            @Override
            public void onFailure(Call<GetUserDetailsResponse> call, Throwable t) {
                Log.e("SAIFUL", "network call not successful", t);
            }
        });
    }
    
    private void goToMainActivity() {
        Toast.makeText(getApplicationContext(), R.string.welcome_to_terawhere, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(FacebookLoginActivity.this, MainActivity.class);
        startActivity(i);
        loginButton.setEnabled(true);
        finish();
    }
}
