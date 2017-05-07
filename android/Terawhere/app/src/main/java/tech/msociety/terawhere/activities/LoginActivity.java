package tech.msociety.terawhere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import tech.msociety.terawhere.R;

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

