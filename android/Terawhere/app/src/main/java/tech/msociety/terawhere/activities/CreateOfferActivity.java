package tech.msociety.terawhere.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import tech.msociety.terawhere.R;

public class CreateOfferActivity extends BaseActivity implements View.OnClickListener {
    private static final String TOOLBAR_TITLE = "Create Offer";

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSave) {
            Toast.makeText(this, "SAVED!!", Toast.LENGTH_SHORT).show();

    ParseObject offers = new ParseObject("Offers");
    offers.put("Name", "musa");

    offers.saveInBackground(new SaveCallback() {
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


        }
         if (view.getId() == R.id.createOfferLinearLayout) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);

        initToolbar(TOOLBAR_TITLE, true);

        LinearLayout backgroundLinearLayout = (LinearLayout) findViewById(R.id.createOfferLinearLayout);
        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(this);
        backgroundLinearLayout.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
