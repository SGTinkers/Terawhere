package tech.msociety.terawhere.screens.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.globals.Constants;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.createuser.FacebookUser;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.abstracts.BaseActivity;

public class FacebookLoginActivity extends BaseActivity implements View.OnClickListener {
    public static final String STRING_EMPTY = "";
    public static final String STRING_SEPARATOR = " ";
    public static final String STRING_FACEBOOK = "facebook";
    public static final String STRING_FACEBOOK_LOG_OUT = "Log out";
    public static final String STRING_CONTINUE_AS = "Continue as ";
    public static final String MESSAGE_LOGGING_IN = "Logging in...";
    public static final String FACEBOOK_TOKEN = "AccessToken";
    public static final String LOG_FACEBOOK_TOKEN = "AccessToken";
    
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    
    private LoginButton loginButton;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        skipCheckForLogin = true;
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook_login);
        
        initializeCallBackManager();
        initializeAccessTokenTracker();
        initializeProfileTracker();
        
        trackAccessTokenTracker();
        trackProfileTracker();
        
        addContinueButtonListener();
        addLoginButtonListener();
        
        initializeContinueButton();
        
        FacebookCallback<LoginResult> callback = getLoginResultFacebookCallback();
        loginButton.registerCallback(callbackManager, callback);
    }
    
    public void onClick(View view) {
        if (isContinueButtonPressed(view)) {
            Log.i(FACEBOOK_TOKEN, AccessToken.getCurrentAccessToken().getToken());
            FacebookUser user = new FacebookUser(AccessToken.getCurrentAccessToken().getToken(), STRING_FACEBOOK);
            Call<FacebookUser> call = TerawhereBackendServer.getApiInstance().createUser(user);
            call.enqueue(new Callback<FacebookUser>() {
                             @Override
                             public void onResponse(Call<FacebookUser> call, Response<FacebookUser> response) {
                                 Constants.SetBearerToken(response.body().getToken());
                                 Toast.makeText(getApplicationContext(), MESSAGE_LOGGING_IN, Toast.LENGTH_SHORT).show();
                                 Profile profile = Profile.getCurrentProfile();
                                 nextActivity(profile);
                             }
    
                @Override
                             public void onFailure(Call<FacebookUser> call, Throwable t) {
        
                }
                         }
            );
    
        }
        
    }
    
    private boolean isContinueButtonPressed(View view) {
        return view.getId() == R.id.button_continue;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Fix this
//        if (AccessToken.getCurrentAccessToken() != null) {
//            Log.i(LOG_FACEBOOK_TOKEN, STRING_SEPARATOR + AccessToken.getCurrentAccessToken().getToken());
//            Token.setToken(AccessToken.getCurrentAccessToken().getToken());
//            Constants.BEARER_TOKEN = response.body().getToken();
//        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    protected void onStop() {
        super.onStop();
        stopAccessTokenTracker();
        stopProfileTracker();
    }
    
    private void stopProfileTracker() {
        profileTracker.stopTracking();
    }
    
    private void stopAccessTokenTracker() {
        accessTokenTracker.stopTracking();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }
    
    private void nextActivity(Profile profile) {
        if (profile != null) {
            Intent main = new Intent(FacebookLoginActivity.this, MainActivity.class);
            finish();
            startActivity(main);
        }
    }
    
    private void initializeContinueButton() {
        if (getLoginButtonText().equals(STRING_FACEBOOK_LOG_OUT)) {
            showContinueButton();
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                String continueButtonText = STRING_CONTINUE_AS + profile.getFirstName() + STRING_SEPARATOR + profile.getLastName();
                continueButton.setText(continueButtonText);
            }
        } else {
            hideContinueButton();
        }
    }
    
    private void hideContinueButton() {
        continueButton.setVisibility(View.INVISIBLE);
    }
    
    @NonNull
    private FacebookCallback<LoginResult> getLoginResultFacebookCallback() {
        return new FacebookCallback<LoginResult>() {
    
            @Override
            public void onSuccess(LoginResult loginResult) {
                showContinueButton();
            }
    
            @Override
            public void onCancel() {
            }
    
            @Override
            public void onError(FacebookException e) {
            }
        };
    }
    
    private void setContinueButtonText(String firstName, String lastName) {
        String continueButtonText = STRING_CONTINUE_AS + firstName + STRING_SEPARATOR + lastName;
        continueButton.setText(continueButtonText);
    }
    
    private void showContinueButton() {
        continueButton.setVisibility(View.VISIBLE);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            setContinueButtonText(profile.getFirstName(), profile.getLastName());
        }
    }
    
    @NonNull
    private String getLoginButtonText() {
        return loginButton.getText().toString();
    }
    
    private void trackProfileTracker() {
        profileTracker.startTracking();
    }
    
    private void trackAccessTokenTracker() {
        accessTokenTracker.startTracking();
    }
    
    private void initializeProfileTracker() {
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
            }
        };
    }
    
    private void initializeAccessTokenTracker() {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if (newToken == null) {
                    hideContinueButton();
                } else {
                    showContinueButton();
                }
            }
        };
    }
    
    private void initializeCallBackManager() {
        callbackManager = CallbackManager.Factory.create();
    }
    
    private void addLoginButtonListener() {
        loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setOnClickListener(this);
    }
    
    private void addContinueButtonListener() {
        continueButton = (Button) findViewById(R.id.button_continue);
        continueButton.setOnClickListener(this);
    }
}
