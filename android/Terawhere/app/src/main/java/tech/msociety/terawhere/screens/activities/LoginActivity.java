package tech.msociety.terawhere.screens.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.crash.FirebaseCrash;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.events.LoginEvent;
import tech.msociety.terawhere.globals.AppPrefs;
import tech.msociety.terawhere.globals.TerawhereApplication;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.createuser.AuthRequestBody;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getuser.GetUserDetailsResponse;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.abstracts.BaseActivity;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static int REQUEST_GOOGLE_SIGN_IN = 1;
    private static String BACKEND_GOOGLE_CLIENT_ID = "145760395523-hrqihi968vadt6jkrgjv6qjfkp6o93j8.apps.googleusercontent.com";
    private CallbackManager callbackManager;
    private ImageButton facebookLoginButton;
    private SignInButton googleLoginButton;
    private GoogleApiClient googleApiClient;

    {
        requireAuth = false;
        requireLocationServices = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (TerawhereApplication.getBearerToken() != null) {
            finish();
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestId()
                .requestServerAuthCode(BACKEND_GOOGLE_CLIENT_ID)
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        facebookLoginButton = (ImageButton) findViewById(R.id.button_facebook_login);
        googleLoginButton = (SignInButton)  findViewById(R.id.button_google_login);
        googleLoginButton.setSize(SignInButton.SIZE_WIDE);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoginActivity", "onSuccess: " + loginResult.getAccessToken().getToken());
                Toast.makeText(LoginActivity.this, R.string.please_wait, Toast.LENGTH_SHORT).show();
                TerawhereBackendServer.getAuthApiInstance().createUser(new AuthRequestBody(loginResult.getAccessToken().getToken(), "facebook")).enqueue(backendAuthRequestCallback);
            }

            @Override
            public void onCancel() {
                Log.d("LoginActivity", "Cancel");
                facebookLoginButton.setEnabled(true);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("LoginActivity", "Error");
                FirebaseCrash.report(error);
                error.printStackTrace();
                Toast.makeText(LoginActivity.this, R.string.error_login_fail_fb, Toast.LENGTH_SHORT).show();
                facebookLoginButton.setEnabled(true);
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
                facebookLoginButton.setEnabled(false);
            }
        });

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGN_IN);
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
        if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                performGoogleAuthWithBackend(account);
            } else {
                Log.d("LoginActivity", "onFailure: Google Sign In Failed. " + result.getStatus().toString());
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void performGoogleAuthWithBackend(GoogleSignInAccount account) {
        Log.d("LoginActivity", "onSuccess: " + account.getServerAuthCode());
        Toast.makeText(LoginActivity.this, R.string.please_wait, Toast.LENGTH_SHORT).show();
        TerawhereBackendServer.getAuthApiInstance().createUser(new AuthRequestBody(account.getServerAuthCode(), "google")).enqueue(backendAuthRequestCallback);
    }

    private Callback<AuthRequestBody> backendAuthRequestCallback = new Callback<AuthRequestBody>() {
        @Override
        public void onResponse(Call<AuthRequestBody> call, Response<AuthRequestBody> response) {
            if (response.isSuccessful()) {
                Log.d("LoginActivity", "Server Token: " + response.body().getToken());
                TerawhereApplication.setBearerToken(response.body().getToken());

                EventBus.getDefault().post(new LoginEvent());
                getUserDetails();
            } else {
                try {
                    TerawhereBackendServer.ErrorDatum.ParseErrorAndToast(LoginActivity.this, response);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }
        }

        @Override
        public void onFailure(Call<AuthRequestBody> call, Throwable t) {
            // TODO: Handle error more elegantly
            TerawhereBackendServer.ErrorDatum.ToastUnknownError(LoginActivity.this, t);
            FirebaseCrash.report(t);
            t.printStackTrace();
            Toast.makeText(LoginActivity.this, R.string.error_login_fail, Toast.LENGTH_SHORT).show();
            facebookLoginButton.setEnabled(true);
        }
    };
    
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
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        facebookLoginButton.setEnabled(true);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
