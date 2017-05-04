package tech.msociety.terawhere.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import tech.msociety.terawhere.R;

/**
 * Created by musa on 3/5/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    Boolean isSignUpModeActive = true;
    TextView textView;
    EditText usernameEditText;
    EditText passwordEditText;

    public void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textView) {
            Button signUpButton = (Button) findViewById(R.id.signUpButton);

            if (isSignUpModeActive) {
                isSignUpModeActive = false;
                signUpButton.setText("Login");
                textView.setText("Or, Signup");
            }
            else {
                isSignUpModeActive = true;
                signUpButton.setText("Signup");
                textView.setText("Or, Login");
            }
        }
        else if (view.getId() == R.id.relativeLayout || view.getId() == R.id.logoImageView) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    public void signUp(View view) {

        if (isNetworkConnected()) {
            if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
                Toast.makeText(this, "A username and password are required.", Toast.LENGTH_SHORT).show();


            } else {
                if (isSignUpModeActive) {


                    ParseUser user = new ParseUser();
                    user.setUsername(usernameEditText.getText().toString());
                    user.setPassword(passwordEditText.getText().toString());

                    // sign up process
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("Signup", "Successful");
                                showMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else { //check if username and password is correct
                    ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                Log.i("Signup", "Login Successful");
                                showMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
        else {
            Toast.makeText(LoginActivity.this, "Network is not connected!", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textView = (TextView) findViewById(R.id.textView);
        //textView.setTextColor(Color.BLACK);
        textView.setOnClickListener(this);

        RelativeLayout backgroundRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);

        backgroundRelativeLayout.setOnClickListener(this);
        logoImageView.setOnClickListener(this);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        passwordEditText.setOnKeyListener(this);

        // check if user has logged in
        if (ParseUser.getCurrentUser() != null) {
            showMainActivity();
        }




        //QUERY
      /*
      ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");

      query.whereEqualTo("username" , "tommy");
      query.setLimit(1);
      query.findInBackground(new FindCallback<ParseObject>() {
                                 @Override
                                 public void done(List<ParseObject> objects, ParseException e) {
                                     if (e == null) {
                                         Log.i("findInBackground", "Retrieved" + objects.size());

                                         if (objects.size() > 0) {
                                             for (ParseObject object : objects) {
                                                 Log.i("findInBackgroundResult", Integer.toString(object.getInt("score")));

                                             }
                                         }
                                     }
                                 }
                             });

                             */

        //ADD EDIT
      /*
    ParseObject score = new ParseObject("Score");
    score.put("username", "musa");
    score.put("score", 86);
    score.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e == null) {
              Log.i("SaveInBackground", "Successful");
            }
            else {
              Log.i("SaveInBackground", "Failed Error: " + e.toString());
            }
        }
    });
    */

      /*
      ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");
      query.getInBackground("OOuSsZe7KP", new GetCallback<ParseObject>() {
                  @Override
                  public void done(ParseObject object, ParseException e) {
                      if(e == null && object != null) {

                          object.put("score", 200);
                          object.saveInBackground();
                          Log.i("ObjectValue" , object.getString("username"));
                          Log.i("ObjectValue", Integer.toString(object.getInt("score")));
                      }
                  }
              });
              */
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUp(view);
        }
        return false;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
}

