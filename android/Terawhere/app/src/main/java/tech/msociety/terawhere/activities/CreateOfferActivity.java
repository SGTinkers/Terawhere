package tech.msociety.terawhere.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tech.msociety.terawhere.R;

public class CreateOfferActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TOOLBAR_TITLE = "Create Offer";
    EditText nameEditText, destinationEditText, seatsAvailableEditText, remarksEditText, vehicleColorEditText, vehiclePlateNumberEditText;
    TimePicker pickUpTimePicker;
    private GoogleApiClient googleApiClient;
    private double lat,lon;
    Location currentLocation;
    public void createOffer(View view) {
        if (isNetworkConnected()) {

            ParseGeoPoint gp = new ParseGeoPoint(lat,lon);
            Calendar cal = Calendar.getInstance();

            cal.set(Calendar.HOUR_OF_DAY, pickUpTimePicker.getCurrentHour());
            cal.set(Calendar.MINUTE, pickUpTimePicker.getCurrentMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date date = cal.getTime();
            String strDate = String.format("Current Date/Time : %tc", date );
            if (nameEditText.getText().toString().matches("") ||
                    destinationEditText.getText().toString().matches("") ||
                    seatsAvailableEditText.getText().toString().matches("") ||
                    remarksEditText.getText().toString().matches("") ||
                    vehicleColorEditText.getText().toString().matches("") ||
                    vehiclePlateNumberEditText.getText().toString().matches("")) {
                Toast.makeText(CreateOfferActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();

            }
            else {
                Log.i("Name:", nameEditText.getText().toString());
                Log.i("Destination:", destinationEditText.getText().toString());
                Log.i("seats:", seatsAvailableEditText.getText().toString());
                Log.i("remarks:", remarksEditText.getText().toString());
                Log.i("vehiclecolor:", vehicleColorEditText.getText().toString());
                Log.i("vehiclenumber:", vehiclePlateNumberEditText.getText().toString());
                Log.i("Date:", strDate);


                ParseObject offers = new ParseObject("Offers");
                offers.put("Name", nameEditText.getText().toString());
                offers.put("Destination", destinationEditText.getText().toString());
                offers.put("SeatsAvailable", Integer.parseInt(seatsAvailableEditText.getText().toString()));
                offers.put("PickUpTime", date);
                offers.put("Remarks", remarksEditText.getText().toString());
                offers.put("VehicleColor", vehicleColorEditText.getText().toString());
                offers.put("PlateNumber", vehiclePlateNumberEditText.getText().toString());
                offers.put("CurrentLocation", gp);


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
                Toast.makeText(this, "Successfully created offer!!", Toast.LENGTH_SHORT).show();

            }
        }
        else {
            Toast.makeText(CreateOfferActivity.this, "Network is not connected!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View view) {

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    1);
        }

        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        destinationEditText = (EditText) findViewById(R.id.destinationEditText);
        seatsAvailableEditText = (EditText) findViewById(R.id.seatsAvailableEditText);
        pickUpTimePicker = (TimePicker) findViewById(R.id.pickUpTimePicker);
        remarksEditText = (EditText) findViewById(R.id.remarksEditText);
        vehicleColorEditText = (EditText) findViewById(R.id.vehicleColorEditText);
        vehiclePlateNumberEditText = (EditText) findViewById(R.id.vehiclePlateNumberEditText);

        LinearLayout backgroundLinearLayout = (LinearLayout) findViewById(R.id.createOfferLinearLayout);
        Button buttonSave = (Button) findViewById(R.id.createOfferButton);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(MainActivity.class.getSimpleName(), "Connected to Google Play Services!");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            currentLocation = lastLocation;
            lat = lastLocation.getLatitude();
            lon = lastLocation.getLongitude();
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(lat, lon, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0)
            {
                TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
                if(addresses.get(0).getAddressLine(0) != null) {
                    Log.i("LOCATION1", addresses.get(0).getAddressLine(0));
                    locationTextView.setText("You are at: " + addresses.get(0).getAddressLine(0));

                }



            }
            else
            {
                // do your staff
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(MainActivity.class.getSimpleName(), "Can't connect to Google Play Services!");
    }



}
