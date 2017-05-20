package tech.msociety.terawhere.screens.activities;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.PostOffers;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.abstracts.ToolbarActivity;

public class CreateOfferActivity extends ToolbarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /******************************************************
     *  Terawhere offer fields. Please do not remove!!! *
     ****************************************************/

    public static final String SEATS_AVAILABLE = "seatsAvailable";

    public static final String DRIVER_REMARKS = "driverRemarks";


    public static final double OFFSET_LATITUDE = 0.000225;
    public static final double OFFSET_LONGITUDE = 0.0043705;

    private static final String TOOLBAR_TITLE = "Create Offer";
    public static final String EDIT_OFFER = "Edit Offer";

    public static final String IS_EDIT = "isEdit";

    public static final String MESSAGE_CREATE_OFFER_SUCCESSFUL = "SUCCESSFULLY CREATED OFFER";
    public static final String MESSAGE_EDIT_OFFER_SUCCESSFUL = "SUCCESSFULLY UPDATED OFFER";

    public static final String LOG_RESPONSE = "messageResponse";

    public static final String MESSAGE_YOU_ARE_AT = "You are at: ";
    public static final String FORMAT_TWO_DIGITS = "%02d";
    public static final String OFFER_ID = "id";

    private boolean isEditOffer = false;

    private double startLocationLatitude;
    private double startLocationLongitude;
    private double endLocationLatitude;
    private double endLocationLongitude;

    private String startLocationName;
    private String endLocationName;


    private int offerId;

    private Button buttonCreateOffer;


    private Button startLocationButton;
    private Button endLocationButton;
    private AutoCompleteTextView editTextVehicleDescription;

    private EditText editTextSeatsAvailable;
    private EditText editTextRemarks;
    private EditText editTextVehiclePlateNumber;

    private EditText editTextVehicleModel;
    private EditText editTextMeetUpTime;
    private GoogleApiClient googleApiClient;
    private String MESSAGE_LOCATION_PERMISSION_NEEDED = "Need location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
        initToolbar(TOOLBAR_TITLE, true);

        buildGoogleApiClient();
        trackCurrentLocation();

        createOfferButtonListener();
        startingLocationTextViewListener();
        endingLocationTextViewListener();

        initializeMeetUpTimeEditText();
        initializeStartingLocationTextView();
        initializeEndingLocationTextView();
        initializeSeatsAvailableEditText();
        initializeRemarksEditText();
        initialializeVehicleDescriptionEditText(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.colors_array)));

        initializeVehicleModelEditText();
        initializeVehicleNumberEditText();


        Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();

        initializeIsEditOffer(bundle, intent);

        if (isEditOffer) {
            initializeOfferId(intent);

            //setMeetUpTimeField(intent);
            setSeatsAvailableField(intent);
            setRemarksField(intent);



            Vehicle vehicle = intent.getParcelableExtra("vehicle");
            editTextVehicleDescription.setText(vehicle.getDescription());
            editTextVehicleModel.setText(vehicle.getModel());
            editTextVehiclePlateNumber.setText(vehicle.getPlateNumber());

            /*setStartingLocationLatitudeField(intent);
            setStartingLocationLongitudeField(intent);
            setStartingLocationNameField(intent);
            setStartingLocationAddressField(intent);
            setEndingLocationLatitudeField(intent);
            setEndingLocationLongitudeField(intent);
            setEndingLocationNameField(intent);
            setEndingLocationAddressField(intent);*/

            TerawhereLocation startTerawhereLocation = intent.getParcelableExtra("startTerawhereLocation");
            TerawhereLocation endTerawhereLocation = intent.getParcelableExtra("endTerawhereLocation");
            startLocationButton.setText(startTerawhereLocation.getAddress());


            endLocationButton.setText(endTerawhereLocation.getAddress());
            startLocationLatitude = startTerawhereLocation.getLatitude();
            startLocationLongitude = startTerawhereLocation.getLongitude();
            endLocationLatitude = endTerawhereLocation.getLatitude();
            endLocationLongitude = endTerawhereLocation.getLongitude();

            final Date meetUpTime = (Date) intent.getSerializableExtra("meetUpTime");
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            editTextMeetUpTime.setText(dateFormat.format(meetUpTime));
            //setGenderPreferenceField(intent);
            setCreateOfferButton();

            editTextMeetUpTime.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int hour = meetUpTime.getHours();
                    int minute = meetUpTime.getMinutes();
                    TimePickerDialog mTimePicker;

                    mTimePicker = new TimePickerDialog(CreateOfferActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String AM_PM = " am";
                            String mm_precede = "";
                            if (selectedHour >= 12) {
                                AM_PM = " pm";
                                if (selectedHour >= 13 && selectedHour < 24) {
                                    selectedHour -= 12;
                                } else {
                                    selectedHour = 12;
                                }
                            } else if (selectedHour == 0) {
                                selectedHour = 12;
                            }
                            if (selectedMinute < 10) {
                                mm_precede = "0";
                            }

                            editTextMeetUpTime.setText(selectedHour + ":" + mm_precede + selectedMinute + AM_PM);
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();

                }
            });
        } else {
            editTextMeetUpTime.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;

                    mTimePicker = new TimePickerDialog(CreateOfferActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String AM_PM = " am";
                            String mm_precede = "";
                            if (selectedHour >= 12) {
                                AM_PM = " pm";
                                if (selectedHour >= 13 && selectedHour < 24) {
                                    selectedHour -= 12;
                                } else {
                                    selectedHour = 12;
                                }
                            } else if (selectedHour == 0) {
                                selectedHour = 12;
                            }
                            if (selectedMinute < 10) {
                                mm_precede = "0";
                            }

                            editTextMeetUpTime.setText(selectedHour + ":" + mm_precede + selectedMinute + AM_PM);
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();

                }
            });
        }
    }


    private void initializeOfferId(Intent intent) {
        offerId = intent.getExtras().getInt(OFFER_ID);
        Log.i(OFFER_ID, " " + offerId);
    }

    private void setCreateOfferButton() {
        buttonCreateOffer.setText(EDIT_OFFER);
    }



    private void setRemarksField(Intent intent) {
        editTextRemarks.setText(getStringValueToEdit(intent, DRIVER_REMARKS));
    }

    private void setSeatsAvailableField(Intent intent) {
        editTextSeatsAvailable.setText(String.format(Locale.getDefault(), "%d", intent.getExtras().getInt(SEATS_AVAILABLE)));
    }


    private double getDoubleValueToEdit(Intent intent, String key) {
        return intent.getExtras().getDouble(key);
    }

    private String getStringValueToEdit(Intent intent, String key) {
        return intent.getStringExtra(key);
    }

    private void initializeIsEditOffer(Bundle bundle, Intent intent) {
        if (bundle != null) {
            isEditOffer = intent.getExtras().getBoolean(IS_EDIT);
        }
    }


    private void initializeVehicleNumberEditText() {
        editTextVehiclePlateNumber = (EditText) findViewById(R.id.edit_text_vehicle_number);
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
    }


    private void initializeVehicleModelEditText() {
        editTextVehicleModel = (EditText) findViewById(R.id.edit_text_vehicle_model);
    }


    private void initialializeVehicleDescriptionEditText(ArrayAdapter<String> adapter) {
        editTextVehicleDescription = (AutoCompleteTextView) findViewById(R.id.edit_text_vehicle_description);
        editTextVehicleDescription.setAdapter(adapter);
        editTextVehicleDescription.setThreshold(1);
    }

    private void initializeRemarksEditText() {
        editTextRemarks = (EditText) findViewById(R.id.edit_text_remarks);
    }

    private void initializeSeatsAvailableEditText() {
        editTextSeatsAvailable = (EditText) findViewById(R.id.edit_text_seats_available);
    }

    private void initializeEndingLocationTextView() {
        endLocationButton = (Button) findViewById(R.id.button_end_location);
    }

    private void initializeStartingLocationTextView() {
        startLocationButton = (Button) findViewById(R.id.button_start_location);
    }

    private void initializeMeetUpTimeEditText() {
        editTextMeetUpTime = (EditText) findViewById(R.id.edit_text_meet_up_time);


    }

    private void trackCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_start_location) {
            try {
                showStartingPlacePickerActivity();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.button_end_location) {
            try {
                showEndingPlacePickerActivity();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.button_create_offer) {

            if (areNotAllFieldsFilled() || !editTextVehiclePlateNumber.getText().toString().matches("^[a-zA-Z0-9]*$")) {

                Toast.makeText(CreateOfferActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            } else {
                if (!isEditOffer) {
                    String date = getDate(); // get todays date
                    String time = editTextMeetUpTime.getText().toString();

                    final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    Date dateObj = null;
                    try {
                        dateObj = sdf.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String meetUpTime = date + " " + new SimpleDateFormat("HH:mm:ss").format(dateObj);
                    if (startLocationName == null) {
                        startLocationName = startLocationButton.getText().toString();
                    }
                    if (endLocationName == null) {
                        endLocationName = endLocationButton.getText().toString();
                    }
                    PostOffers postOffers = new PostOffers(meetUpTime, startLocationName,
                            startLocationButton.getText().toString(), startLocationLatitude,
                            startLocationLongitude, endLocationName, endLocationButton.getText().toString(),
                            endLocationLatitude, endLocationLongitude, Integer.parseInt(editTextSeatsAvailable.getText().toString()),
                            editTextRemarks.getText().toString(), editTextVehiclePlateNumber.getText().toString(),
                            editTextVehicleDescription.getText().toString(), editTextVehicleModel.getText().toString());

                    Log.i("meetUpTime", " : " + meetUpTime);
                    Log.i("StartingName", ":" + startLocationName);
                    Log.i("StartingAddress", ":" + startLocationButton.getText().toString());
                    Log.i("StartingLatitude", ":" + startLocationLatitude);
                    Log.i("StartingLongitude", ":" + startLocationLongitude);
                    Log.i("EndingName", ":" + endLocationName);
                    Log.i("EndingAddress", ":" + endLocationButton.getText().toString());
                    Log.i("EndingLatitue", ":" + endLocationLatitude);
                    Log.i("EndingLongitude", ":" + endLocationLongitude);
                        Log.i("SeatsAvailable",":" + Integer.parseInt(editTextSeatsAvailable.getText().toString()));
                        Log.i("Remarks",":" + editTextRemarks.getText().toString());
                        Log.i("VehiclePlateNumber",":" + editTextVehiclePlateNumber.getText().toString());
                        Log.i("VehicleDesc",":" + editTextVehicleDescription.getText().toString());
                        Log.i("VehicleModel",":" + editTextVehicleModel.getText().toString());

                    Call<Void> call = createOfferApi(postOffers);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            if (response.isSuccessful()) {
                                Log.i(LOG_RESPONSE, ": " + response.message());
                                Toast.makeText(getApplicationContext(), MESSAGE_CREATE_OFFER_SUCCESSFUL, Toast.LENGTH_SHORT).show();

                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("FirstTab", 4);
                                setResult(RESULT_OK, resultIntent);
                                finish();

                            } else {
                                try {
                                    Log.i(LOG_RESPONSE, ": " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });

                } else {

                    String meetUpTime = "";
                    PostOffers postOffers = new PostOffers(meetUpTime, startLocationName,
                            startLocationButton.getText().toString(), startLocationLatitude, startLocationLongitude,
                            endLocationName, endLocationButton.getText().toString(),
                            endLocationLatitude, endLocationLongitude, Integer.parseInt(editTextSeatsAvailable.getText().toString()),
                            editTextRemarks.getText().toString(),
                            editTextVehiclePlateNumber.getText().toString(), editTextVehicleDescription.getText().toString(),
                            editTextVehicleModel.getText().toString());

                    Call<Void> call = TerawhereBackendServer.getApiInstance().editOffer(offerId, postOffers);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            if (response.isSuccessful()) {
                                Log.i("EDIT_MESSAGE", ": " + response.message());
                                Toast.makeText(getApplicationContext(), MESSAGE_EDIT_OFFER_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("FirstTab", 4);
                                setResult(RESULT_OK, resultIntent);
                                finish();

                            } else {
                                try {
                                    Log.i("EDIT_ERROR", ": " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });

                }
            }

        }
    }

    private String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }


    private Call<Void> createOfferApi(PostOffers postOffers) {
        return TerawhereBackendServer.getApiInstance().createOffer(postOffers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean areNotAllFieldsFilled() {
        return (startLocationLatitude == 0.0 || startLocationLongitude == 0.0 || endLocationLatitude == 0.0 || endLocationLongitude == 0.0 || editTextSeatsAvailable.getText().toString().matches("") || editTextRemarks.getText().toString().matches("") || editTextVehicleDescription.getText().toString().matches("") || editTextVehiclePlateNumber.getText().toString().matches(""));

    }

    private void showStartingPlacePickerActivity() throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        initializePlacePickerMap(intentBuilder, startLocationLatitude, startLocationLongitude);
        startPlacePickerActivity(intentBuilder, 1);
    }

    private void showEndingPlacePickerActivity() throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        if (isEndingLocationInitialized()) {
            initializePlacePickerMap(intentBuilder, endLocationLatitude, endLocationLongitude);
        } else {
            initializePlacePickerMap(intentBuilder, startLocationLatitude, startLocationLongitude);
        }
        startPlacePickerActivity(intentBuilder, 2);
    }

    private boolean isEndingLocationInitialized() {
        return (endLocationLatitude != 0.0 || endLocationLongitude != 0.0);
    }

    private void initializePlacePickerMap(PlacePicker.IntentBuilder intentBuilder, double locationLatitude, double locationLongitude) {
        LatLng minimumBound = new LatLng(locationLatitude - OFFSET_LATITUDE, locationLongitude - OFFSET_LONGITUDE);
        LatLng maximumBound = new LatLng(locationLatitude + OFFSET_LATITUDE, locationLongitude + OFFSET_LONGITUDE);
        LatLngBounds placePickerMapBounds = new LatLngBounds(minimumBound, maximumBound);
        intentBuilder.setLatLngBounds(placePickerMapBounds);
    }

    private void startPlacePickerActivity(PlacePicker.IntentBuilder intentBuilder, int requestCode) throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        Intent intent = intentBuilder.build(CreateOfferActivity.this);
        startActivityForResult(intent, requestCode);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * on click listeners
     */


    private void startingLocationTextViewListener() {
        TextView locationTextView = (TextView) findViewById(R.id.button_start_location);
        locationTextView.setOnClickListener(this);
    }

    private void endingLocationTextViewListener() {
        TextView endingLocationTextView = (TextView) findViewById(R.id.button_end_location);
        endingLocationTextView.setOnClickListener(this);
    }


    private void createOfferButtonListener() {
        buttonCreateOffer = (Button) findViewById(R.id.button_create_offer);
        buttonCreateOffer.setOnClickListener(this);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, MESSAGE_LOCATION_PERMISSION_NEEDED, Toast.LENGTH_SHORT).show();
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

    // Connection with Google Play Services successful
    @Override
    public void onConnected(Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            startLocationLatitude = lastLocation.getLatitude();
            startLocationLongitude = lastLocation.getLongitude();
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(startLocationLatitude, startLocationLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                if (addresses.size() > 0) {
                    if (addresses.get(0).getAddressLine(0) != null) {
                        String strAddress = addresses.get(0).getAddressLine(0);
                        startLocationButton.setText(strAddress);
                    }
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    // Connection with Google Play Services failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence address = place.getAddress();
            startLocationLatitude = place.getLatLng().latitude;
            startLocationLongitude = place.getLatLng().longitude;
            startLocationButton.setText(address);
            if (!(place.getName().toString().contains("\"N") || place.getName().toString().contains("\"E") || place.getName().toString().contains("\"S") || place.getName().toString().contains("\"W"))) {
                //textViewStartingLocation.append("\n" + place.getName());
                //startLocationName = place.getName().toString();
            }

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence address = place.getAddress();
            endLocationLatitude = place.getLatLng().latitude;
            endLocationLongitude = place.getLatLng().longitude;
            endLocationButton.setText(address);
            if (!(place.getName().toString().contains("\"N") || place.getName().toString().contains("\"E") || place.getName().toString().contains("\"S") || place.getName().toString().contains("\"W"))) {
                //textViewStartingLocation.append("\n" + place.getName());
                //endLocationName = place.getName().toString();

            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
