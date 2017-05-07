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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import tech.msociety.terawhere.R;

public class CreateOfferActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TOOLBAR_TITLE = "Create Offer";

    private EditText editTextSeatsAvailable, editTextRemarks, editTextVehiclePlateNumber;
    private AutoCompleteTextView editTextDestination, editTextVehicleColor;
    private TimePicker timePickerPickUpTime;
    private TextView textViewLocation;
    private GoogleApiClient googleApiClient;
    private Button buttonCreateOffer;
    private double currentLocationLatitude, currentLocationLongitude;
    private boolean isEditOffer = false;

    private String offerId;

    private String[] mosques = {"Yusuf Ishak", "Darul Makmur", "An-Nur", "Al-Muttaqin",
            "As-Syafaah", "Al-Falah", "Al-Mukminin", "Al-Maarof", "Al-Iman", "Al-Mawaddah",
            "Al-Istighfar", "Ahmad Ibrahim", "Al-Firdaus", "Al-Istiqamah", "Al-Ansar", "Al-Abrar", "Al-Huda", "Al-Amin"};

    private String[] color = {"red", "blue", "black", "white", "yellow", "gray", "green", "silver", "dark blue", "dark green", "purple", "orange", "brown"};

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.createOfferLinearLayout) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }

        if (view.getId() == R.id.imageCurrentLocation || view.getId() == R.id.locationTextView) {
            try {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                intentBuilder.setLatLngBounds(new LatLngBounds(
                        new LatLng(currentLocationLatitude - 0.016225, currentLocationLongitude - 0.1043705), new LatLng(currentLocationLatitude + 0.016225, currentLocationLongitude + 0.1043705)));
                Intent intent = intentBuilder.build(CreateOfferActivity.this);
                startActivityForResult(intent, 1);

            } catch (GooglePlayServicesRepairableException
                    | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
        if (view.getId() == R.id.createOfferButton) {
            if (isNetworkConnected()) {

                final ParseGeoPoint gp = new ParseGeoPoint(currentLocationLatitude, currentLocationLongitude);
                Calendar calendar = Calendar.getInstance();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    calendar.set(Calendar.HOUR_OF_DAY, timePickerPickUpTime.getHour());
                    calendar.set(Calendar.MINUTE, timePickerPickUpTime.getMinute());
                }

                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                final Date date = calendar.getTime();
                if (editTextDestination.getText().toString().matches("") ||
                        editTextSeatsAvailable.getText().toString().matches("") ||
                        editTextRemarks.getText().toString().matches("") ||
                        editTextVehicleColor.getText().toString().matches("") ||
                        editTextVehiclePlateNumber.getText().toString().matches("")) {
                    Toast.makeText(CreateOfferActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();

                } else {
                    if (!isEditOffer) {


                        ParseObject offers = new ParseObject("Offers");
                        offers.put("Name", ParseUser.getCurrentUser().getString("username"));

                        offers.put("Destination", editTextDestination.getText().toString());
                        offers.put("SeatsAvailable", Integer.parseInt(editTextSeatsAvailable.getText().toString()));
                        offers.put("PickUpTime", date);
                        offers.put("Remarks", editTextRemarks.getText().toString());
                        offers.put("VehicleColor", editTextVehicleColor.getText().toString());
                        offers.put("PlateNumber", editTextVehiclePlateNumber.getText().toString());
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
                    } else {
                        if (!offerId.isEmpty()) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Offers");
                            query.getInBackground(offerId, new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null && object != null) {
                                        object.put("Name", ParseUser.getCurrentUser().getString("username"));
                                        object.put("Destination", editTextDestination.getText().toString());
                                        object.put("SeatsAvailable", Integer.parseInt(editTextSeatsAvailable.getText().toString()));
                                        object.put("PickUpTime", date);
                                        object.put("Remarks", editTextRemarks.getText().toString());
                                        object.put("VehicleColor", editTextVehicleColor.getText().toString());
                                        object.put("PlateNumber", editTextVehiclePlateNumber.getText().toString());
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
            } else {
                Toast.makeText(CreateOfferActivity.this, "Network is not connected!", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);

        initToolbar(TOOLBAR_TITLE, true);
        textViewLocation = (TextView) findViewById(R.id.locationTextView);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        editTextDestination = (AutoCompleteTextView) findViewById(R.id.destinationEditText);

        ArrayAdapter<String> adapter = new
                ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mosques);

        editTextDestination.setAdapter(adapter);
        editTextDestination.setThreshold(1);

        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
        editTextSeatsAvailable = (EditText) findViewById(R.id.seatsAvailableEditText);
        timePickerPickUpTime = (TimePicker) findViewById(R.id.pickUpTimePicker);
        editTextRemarks = (EditText) findViewById(R.id.remarksEditText);
        editTextVehicleColor = (AutoCompleteTextView) findViewById(R.id.vehicleColorEditText);
        ArrayAdapter<String> adapter2 = new
                ArrayAdapter<>(this, android.R.layout.simple_list_item_1, color);

        editTextVehicleColor.setAdapter(adapter2);
        editTextVehicleColor.setThreshold(1);
        editTextVehiclePlateNumber = (EditText) findViewById(R.id.vehiclePlateNumberEditText);
        Bundle bundle = getIntent().getExtras();

        Intent intent = getIntent();
        if (bundle != null) {
            isEditOffer = intent.getExtras().getBoolean("isEdit");
        }

        if (isEditOffer) {
            Date pickUpTime = (Date) intent.getSerializableExtra("pickUpTime");
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(pickUpTime);   // assigns calendar to given date
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePickerPickUpTime.setHour(calendar.get(Calendar.HOUR));
                timePickerPickUpTime.setMinute(calendar.get(Calendar.MINUTE));

            }



            offerId = intent.getStringExtra("id");
            Log.i("OFFER ID: ", offerId);
            editTextDestination.setText(intent.getStringExtra("destination"));
            editTextSeatsAvailable.setText(String.format(Locale.getDefault(), "%d", intent.getExtras().getInt("seatsAvailable")));
            editTextRemarks.setText(intent.getStringExtra("remarks"));
            editTextVehicleColor.setText(intent.getStringExtra("vehicleColor"));
            editTextVehiclePlateNumber.setText(intent.getStringExtra("vehiclePlateNumber"));
            buttonCreateOffer.setText("Edit Offer");

        }
        buttonCreateOfferListener();
        linearLayoutCreateOfferListener();
        textViewCurrentLocationListener();
        imageViewCurrentLocationListener();


    }

    private void imageViewCurrentLocationListener() {
        ImageView imageCurrentLocation = (ImageView) findViewById(R.id.imageCurrentLocation);
        imageCurrentLocation.setOnClickListener(this);
    }

    private void textViewCurrentLocationListener() {
        TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
        locationTextView.setOnClickListener(this);
    }

    private void linearLayoutCreateOfferListener() {
        LinearLayout backgroundLinearLayout = (LinearLayout) findViewById(R.id.createOfferLinearLayout);
        backgroundLinearLayout.setOnClickListener(this);
    }

    private void buttonCreateOfferListener() {
        buttonCreateOffer = (Button) findViewById(R.id.createOfferButton);
        buttonCreateOffer.setOnClickListener(this);
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
        return ni != null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
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
            currentLocationLatitude = lastLocation.getLatitude();
            currentLocationLongitude = lastLocation.getLongitude();
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(currentLocationLatitude, currentLocationLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                if (addresses.size() > 0) {

                    if (addresses.get(0).getAddressLine(0) != null) {
                        Log.i("LOCATION1", addresses.get(0).getAddressLine(0));
                        String strAddress = "You are at: " + addresses.get(0).getAddressLine(0);
                        textViewLocation.setText(strAddress);

                    }


                }
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(MainActivity.class.getSimpleName(), "Can't connect to Google Play Services!");
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == 1
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence address = place.getAddress();

            currentLocationLatitude = place.getLatLng().latitude;
            currentLocationLongitude = place.getLatLng().longitude;
            textViewLocation.setText(address);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
