package tech.msociety.terawhere.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tech.msociety.terawhere.R;

public class CreateOfferActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TOOLBAR_TITLE = "Create Offer";
    EditText seatsAvailableEditText, remarksEditText, vehiclePlateNumberEditText;
    AutoCompleteTextView destinationEditText, vehicleColorEditText;
    TimePicker pickUpTimePicker;
    TextView locationTextView;
    private GoogleApiClient googleApiClient;
    private double lat,lon;
    Location currentLocation;
    Button buttonSave;
    boolean isEdit = false;
    String offerId = "";
    String[] mosques = { "Yusuf Ishak", "Darul Makmur", "An-Nur", "Al-Muttaqin",
            "As-Syafaah", "Al-Falah", "Al-Mukminin", "Al-Maarof", "Al-Iman", "Al-Mawaddah",
            "Al-Istighfar", "Ahmad Ibrahim", "Al-Firdaus", "Al-Istiqamah", "Al-Ansar","Al-Abrar", "Al-Huda", "Al-Amin" };

    String[] color = { "red", "blue", "black", "white", "yellow", "gray", "green", "silver", "dark blue", "dark green", "purple", "orange" ,"brown"};
    public void onChangeLocation(View view) {

    }
    public void createOffer(View view) {
        if (isNetworkConnected()) {

            final ParseGeoPoint gp = new ParseGeoPoint(lat,lon);
            Calendar cal = Calendar.getInstance();

            cal.set(Calendar.HOUR_OF_DAY, pickUpTimePicker.getCurrentHour());
            cal.set(Calendar.MINUTE, pickUpTimePicker.getCurrentMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            final Date date = cal.getTime();
            String strDate = String.format("Current Date/Time : %tc", date );
            if (destinationEditText.getText().toString().matches("") ||
                    seatsAvailableEditText.getText().toString().matches("") ||
                    remarksEditText.getText().toString().matches("") ||
                    vehicleColorEditText.getText().toString().matches("") ||
                    vehiclePlateNumberEditText.getText().toString().matches("")) {
                Toast.makeText(CreateOfferActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();

            }
            else {
                if (!isEdit) {


                    ParseObject offers = new ParseObject("Offers");
                    offers.put("Name", ParseUser.getCurrentUser().getString("username"));

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
                            } else {
                                Log.i("SaveInBackground", "Failed Error: " + e.toString());
                            }
                        }
                    });
                    Toast.makeText(this, "Successfully created offer!!", Toast.LENGTH_SHORT).show();
                }
                else {
                        if (!offerId.isEmpty()) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Offers");
                            query.getInBackground(offerId, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null && object != null) {
                                        object.put("Name", ParseUser.getCurrentUser().getString("username"));
                                        object.put("Destination", destinationEditText.getText().toString());
                                        object.put("SeatsAvailable", Integer.parseInt(seatsAvailableEditText.getText().toString()));
                                        object.put("PickUpTime", date);
                                        object.put("Remarks", remarksEditText.getText().toString());
                                        object.put("VehicleColor", vehicleColorEditText.getText().toString());
                                        object.put("PlateNumber", vehiclePlateNumberEditText.getText().toString());
                                        object.put("CurrentLocation", gp);
                                        object.saveInBackground();

                                    }

                                }
                            });
                        }
                    Toast.makeText(this, "Successfully updated offer!!", Toast.LENGTH_SHORT).show();

                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        Intent intent = new Intent(CreateOfferActivity.this, MainActivity.class);
                        intent.putExtra("FirstTab", 4);
                        finish();
                        startActivity(intent);
                    }
                }, 1000);



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

        if (view.getId() == R.id.imageCurrentLocation) {
            try {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                intentBuilder.setLatLngBounds(new LatLngBounds(
                        new LatLng(lat - 0.016225, lon - 0.1043705), new LatLng(lat + 0.016225, lon + 0.1043705)));
                Intent intent = intentBuilder.build(CreateOfferActivity.this);
                startActivityForResult(intent, 1);

            } catch (GooglePlayServicesRepairableException
                    | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);

        initToolbar(TOOLBAR_TITLE, true);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    1);
        }

        destinationEditText=(AutoCompleteTextView) findViewById(R.id.destinationEditText);

        ArrayAdapter adapter = new
                ArrayAdapter(this,android.R.layout.simple_list_item_1,mosques);

        destinationEditText.setAdapter(adapter);
        destinationEditText.setThreshold(1);

        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
        seatsAvailableEditText = (EditText) findViewById(R.id.seatsAvailableEditText);
        pickUpTimePicker = (TimePicker) findViewById(R.id.pickUpTimePicker);
        remarksEditText = (EditText) findViewById(R.id.remarksEditText);
        vehicleColorEditText = (AutoCompleteTextView) findViewById(R.id.vehicleColorEditText);
        ArrayAdapter adapter2 = new
                ArrayAdapter(this,android.R.layout.simple_list_item_1,color);

        vehicleColorEditText.setAdapter(adapter2);
        vehicleColorEditText.setThreshold(1);
        vehiclePlateNumberEditText = (EditText) findViewById(R.id.vehiclePlateNumberEditText);
        buttonSave = (Button) findViewById(R.id.createOfferButton);
        Bundle bundle= getIntent().getExtras();

        Intent intent = getIntent();
        if (bundle != null) {
            isEdit = intent.getExtras().getBoolean("isEdit");
        }

        if (isEdit) {
            Date pickUpTime = (Date)intent.getSerializableExtra("pickUpTime");
            pickUpTimePicker.setCurrentHour(pickUpTime.getHours());
            pickUpTimePicker.setCurrentMinute(pickUpTime.getMinutes());
            offerId = intent.getStringExtra("id");
            Log.i("OFFER ID: " , offerId);
            destinationEditText.setText(intent.getStringExtra("destination"));
            seatsAvailableEditText.setText(Integer.toString(intent.getExtras().getInt("seatsAvailable")));
            remarksEditText.setText(intent.getStringExtra("remarks"));
            vehicleColorEditText.setText(intent.getStringExtra("vehicleColor"));
            vehiclePlateNumberEditText.setText(intent.getStringExtra("vehiclePlateNumber"));
            buttonSave.setText("Edit Offer");

        }
        LinearLayout backgroundLinearLayout = (LinearLayout) findViewById(R.id.createOfferLinearLayout);

        backgroundLinearLayout.setOnClickListener(this);
        ImageView imageCurrentLocation = (ImageView) findViewById(R.id.imageCurrentLocation);
        imageCurrentLocation.setOnClickListener(this);


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

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == 1
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }
            lat = place.getLatLng().latitude;
            lon = place.getLatLng().longitude;
            locationTextView.setText(address);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
