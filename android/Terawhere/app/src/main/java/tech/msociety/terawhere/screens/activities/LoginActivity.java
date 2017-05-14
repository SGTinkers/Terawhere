package tech.msociety.terawhere.screens.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.FacebookUser;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;


/******************* ATTENTION ******************
 *
 * This file will not be use anymore, but will be keep in place
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * */

public class LoginActivity extends AppCompatActivity {
    TextView textView;
    EditText usernameEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViewHandles();
        attachClickListenerToLoginButton();
        callFacebook();
    }

    private void callFacebook() {
        FacebookUser user = new FacebookUser("EAAEH3ZC3aok0BAAq25MkWKDGpZCQqyn4Yac48zm4ZBVrdj79aubAWcbCxLVdlYST0EOESAuEQZAwsJc5gPM9a4tow23Vmc53aIs0ZBZCbf6ihgbVnjLyy9RfZAFMHphkYgoxgn42DytSbZBeESd3WjVzBbSWeUwZCJabZBY7Cy10Tgptm9je6ZBIqtouwjlsrHNoFEgxAifi3FEqsRfkIaZC96Xg", "facebook");
        Call<FacebookUser> call = TerawhereBackendServer.getApiInstance("").createUser(user);
        call.enqueue(new Callback<FacebookUser>() {
                         @Override
                         public void onResponse(Call<FacebookUser> call, Response<FacebookUser> response) {
                             Log.i("Response", response.body().toString());
                         }

                         @Override
                         public void onFailure(Call<FacebookUser> call, Throwable t) {

                         }
                     }
        );
    }

    private void initViewHandles() {
        textView = (TextView) findViewById(R.id.textView);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
    }

    private void attachClickListenerToLoginButton() {
        Button signUpButton = (Button) findViewById(R.id.buttonLogin);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1) check login credentials are legit - if legit, get instance of user
                // 2) save user instance in SharedPreferences
                // 3) start MainActivity
                segueToMainActivity();
            }
        });
    }

    public void segueToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

