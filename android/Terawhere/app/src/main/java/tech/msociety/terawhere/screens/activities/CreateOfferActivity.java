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
import android.os.Build;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.OffersDatum;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.abstracts.ToolbarActivity;

public class CreateOfferActivity extends ToolbarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final double OFFSET_LATITUDE = 0.000225;
    public static final double OFFSET_LONGITUDE = 0.0043705;

    private static final String TOOLBAR_TITLE = "Create Offer";
    public static final String ENDING_LOCATION_LONGITUDE = "endingLocationLongitude";
    public static final String ENDING_LOCATION_LATITUDE = "endingLocationLatitude";
    public static final String ENDING_LOCATION_NAME = "endingLocationName";
    public static final String ENDING_LOCATION_ADDRESS = "endingLocationAddress";
    public static final String STARTING_LOCATION_ADDRESS = "startingLocationAddress";
    public static final String STARTING_LOCATION_NAME = "startingLocationName";
    public static final String STARTING_LOCATION_LONGITUDE = "startingLocationLongitude";
    public static final String STARTING_LOCATION_LATITUDE = "startingLocationLatitude";
    public static final String VEHICLE_NUMBER = "vehicleNumber";
    public static final String VEHICLE_DESCRIPTION = "vehicleDescription";
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    public static final String IS_EDIT = "isEdit";
    public static final String JWT_TOKEN = "jwtToken";
    public static final String MEET_UP_TIME = "meetUpTime";
    public static final String YYYY_DD_MM_HH_MM = "yyyy-dd-MM HH:mm";
    public static final String VEHICLE_MODEL = "vehicleModel";
    public static final String DRIVER_REMARKS = "driverRemarks";
    public static final String SEATS_AVAILABLE = "seatsAvailable";
    public static final String GENDER_PREFERENCE = "genderPreference";
    public static final String EDIT_OFFER = "Edit Offer";
    public static final String MESSAGE_CREATE_OFFER_SUCCESSFUL = "SUCCESSFULLY CREATED OFFER";
    public static final String MESSAGE_RESPONSE = "messageResponse";
    public static final String MESSAGE_EDIT_OFFER_SUCCESSFUL = "Successfully updated offer!!";
    public static final String MESSAGE_NETWORK_ERROR = "Network is not connected!";
    public static final String MESSAGE_LOCATION_PERMISSION_NEEDED = "Need your location!";
    public static final String MESSAGE_YOU_ARE_AT = "You are at: ";
    public static final String FORMAT_TWO_DIGITS = "%02d";
    public static final String OFFER_ID = "id";

    private boolean isEditOffer = false;

    private double startingLocationLatitude;
    private double startingLocationLongitude;
    private double endingLocationLatitude;
    private double endingLocationLongitude;

    private int offerId;

    private Button buttonCreateOffer;

    private RadioGroup genderRadioGroup;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;

    private TextView textViewStartingLocation;
    private TextView textViewEndingLocation;
    private AutoCompleteTextView editTextVehicleDescription;

    private EditText editTextSeatsAvailable;
    private EditText editTextRemarks;
    private EditText editTextVehiclePlateNumber;
    private EditText editTextStartingLocationName;
    private EditText editTextEndingLocationName;
    private EditText editTextVehicleModel;
    private EditText editTextMeetUpTime;
    private TimePicker timePickerMeetUpTime;
    private GoogleApiClient googleApiClient;

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

            setMeetUpTimeField(intent);
            setSeatsAvailableField(intent);
            setRemarksField(intent);

          /*  setVehicleDescriptionField(intent);
            setVehicleNumberField(intent);
            setVehicleModelField(intent);*/

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
            editTextStartingLocationName.setText(startTerawhereLocation.getName());
            textViewStartingLocation.setText(startTerawhereLocation.getAddress());


            editTextEndingLocationName.setText(endTerawhereLocation.getName());
            textViewEndingLocation.setText(endTerawhereLocation.getAddress());
            startingLocationLatitude = startTerawhereLocation.getLatitude();
            startingLocationLongitude = startTerawhereLocation.getLongitude();
            endingLocationLatitude = endTerawhereLocation.getLatitude();
            endingLocationLongitude = endTerawhereLocation.getLongitude();

            //setGenderPreferenceField(intent);
            setCreateOfferButton();
        }
    }


    private void initializeOfferId(Intent intent) {
        offerId = intent.getExtras().getInt(OFFER_ID);
        Log.i(OFFER_ID, " " + offerId);
    }

    private void setEndingLocationLongitudeField(Intent intent) {
        endingLocationLongitude = getDoubleValueToEdit(intent, ENDING_LOCATION_LONGITUDE);
    }

    private void setEndingLocationLatitudeField(Intent intent) {
        endingLocationLatitude = getDoubleValueToEdit(intent, ENDING_LOCATION_LATITUDE);
    }

    private void setStartingLocationLongitudeField(Intent intent) {
        startingLocationLongitude = getDoubleValueToEdit(intent, STARTING_LOCATION_LONGITUDE);
    }

    private void setStartingLocationLatitudeField(Intent intent) {
        startingLocationLatitude = getDoubleValueToEdit(intent, STARTING_LOCATION_LATITUDE);
    }

    private void setCreateOfferButton() {
        buttonCreateOffer.setText(EDIT_OFFER);
    }

    private void setGenderPreferenceField(Intent intent) {
        String gender = getStringValueToEdit(intent, GENDER_PREFERENCE);
        if (gender.toLowerCase().equals(MALE)) {
            radioButtonMale.setChecked(true);
        } else if (gender.toLowerCase().equals(FEMALE)) {
            radioButtonFemale.setChecked(true);
        }
    }



    private void setEndingLocationAddressField(Intent intent) {
        textViewEndingLocation.setText(getStringValueToEdit(intent, ENDING_LOCATION_ADDRESS));
    }

    private void setEndingLocationNameField(Intent intent) {
        editTextEndingLocationName.setText(getStringValueToEdit(intent, ENDING_LOCATION_NAME));
    }

    private void setStartingLocationAddressField(Intent intent) {
        textViewStartingLocation.setText(getStringValueToEdit(intent, STARTING_LOCATION_ADDRESS));
    }

    private void setStartingLocationNameField(Intent intent) {
        editTextStartingLocationName.setText(getStringValueToEdit(intent, STARTING_LOCATION_NAME));
    }

    private void setVehicleModelField(Intent intent) {
        editTextVehicleModel.setText(getStringValueToEdit(intent, VEHICLE_MODEL));
    }

    private void setVehicleNumberField(Intent intent) {
        editTextVehiclePlateNumber.setText(getStringValueToEdit(intent, VEHICLE_NUMBER));
    }

    private void setVehicleDescriptionField(Intent intent) {
        editTextVehicleDescription.setText(getStringValueToEdit(intent, VEHICLE_DESCRIPTION));
    }

    private void setRemarksField(Intent intent) {
        editTextRemarks.setText(getStringValueToEdit(intent, DRIVER_REMARKS));
    }

    private void setSeatsAvailableField(Intent intent) {
        editTextSeatsAvailable.setText(String.format(Locale.getDefault(), "%d", intent.getExtras().getInt(SEATS_AVAILABLE)));
    }

    private void setMeetUpTimeField(Intent intent) {
        Date meetUpTime = (Date) intent.getSerializableExtra(MEET_UP_TIME);
        //SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_DD_MM_HH_MM);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePickerMeetUpTime.setHour(meetUpTime.getHours());
            timePickerMeetUpTime.setMinute(meetUpTime.getMinutes());

        } else {
            timePickerMeetUpTime.setCurrentHour(meetUpTime.getHours());
            timePickerMeetUpTime.setCurrentMinute(meetUpTime.getMinutes());
        }
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
        editTextVehiclePlateNumber = (EditText) findViewById(R.id.vehiclePlateNumberEditText);
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
    }


    private void initializeVehicleModelEditText() {
        editTextVehicleModel = (EditText) findViewById(R.id.vehicleModelEditText);
    }


    private void initialializeVehicleDescriptionEditText(ArrayAdapter<String> adapter) {
        editTextVehicleDescription = (AutoCompleteTextView) findViewById(R.id.vehicleDescriptionEditText);
        editTextVehicleDescription.setAdapter(adapter);
        editTextVehicleDescription.setThreshold(1);
    }

    private void initializeRemarksEditText() {
        editTextRemarks = (EditText) findViewById(R.id.remarksEditText);
    }

    private void initializeSeatsAvailableEditText() {
        editTextSeatsAvailable = (EditText) findViewById(R.id.seatsAvailableEditText);
    }

    private void initializeEndingLocationTextView() {
        textViewEndingLocation = (TextView) findViewById(R.id.endingLocationTextView);
    }

    private void initializeStartingLocationTextView() {
        textViewStartingLocation = (TextView) findViewById(R.id.locationTextView);
    }

    private void initializeMeetUpTimeEditText() {
        editTextMeetUpTime = (EditText) findViewById(R.id.meetUpTimeEditText);

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
                        String AM_PM = " AM";
                        String mm_precede = "";
                        if (selectedHour >= 12) {
                            AM_PM = " PM";
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
                        editTextMeetUpTime.setText("Meet up time: " + selectedHour + ":" + mm_precede + selectedMinute + " " + AM_PM);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
    }

    private void trackCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.locationTextView) {
            try {
                showStartingPlacePickerActivity();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.endingLocationTextView) {
            try {
                showEndingPlacePickerActivity();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.createOfferButton) {
            if (isNetworkConnected()) {

                if (areNotAllFieldsFilled() || !editTextVehiclePlateNumber.getText().toString().matches("^[a-zA-Z0-9]*$")) {

                    Toast.makeText(CreateOfferActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isEditOffer) {
                        String date = getDate();
                        String hour = getHourToEdit();
                        String minute = getMinuteToEdit();

                        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                        RadioButton radioButtonGender = (RadioButton) findViewById(selectedId);

                        String meetUpTime = date + " " + hour + ":" + minute;
                        OffersDatum offer = new OffersDatum(meetUpTime, editTextStartingLocationName.getText().toString(), textViewStartingLocation.getText().toString(), startingLocationLatitude, startingLocationLongitude, editTextEndingLocationName.getText().toString(), textViewEndingLocation.getText().toString(), endingLocationLatitude, endingLocationLongitude, Integer.parseInt(editTextSeatsAvailable.getText().toString()), editTextRemarks.getText().toString(), 1, radioButtonGender.getText().toString().toLowerCase(), editTextVehiclePlateNumber.getText().toString(), editTextVehicleDescription.getText().toString(), editTextVehicleModel.getText().toString());

                        /*Log.i("meetUpTime"," : " + meetUpTime);
                        Log.i("StartingName",":" + editTextStartingLocationName.getText().toString());
                        Log.i("StartingAddress",":" + textViewStartingLocation.getText().toString());
                        Log.i("StartingLatitude",":" + startingLocationLatitude);
                        Log.i("StartingLongitude",":" + startingLocationLongitude);
                        Log.i("EndingName",":" + editTextEndingLocationName.getText().toString());
                        Log.i("EndingAddress",":" + textViewEndingLocation.getText().toString());
                        Log.i("EndingLatitue",":" + endingLocationLatitude);
                        Log.i("EndingLongitude",":" + endingLocationLongitude);
                        Log.i("SeatsAvailable",":" + Integer.parseInt(editTextSeatsAvailable.getText().toString()));
                        Log.i("Remarks",":" + editTextRemarks.getText().toString());
                        Log.i("GenderPref",":" + radioButtonGender.getText().toString());
                        Log.i("VehiclePlateNumber",":" + editTextVehiclePlateNumber.getText().toString());
                        Log.i("VehicleDesc",":" + editTextVehicleDescription.getText().toString());
                        Log.i("VehicleModel",":" + editTextVehicleModel.getText().toString());
*/
                        Call<OffersDatum> call = createOfferApi(offer);
                        call.enqueue(new Callback<OffersDatum>() {
                            @Override
                            public void onResponse(Call<OffersDatum> call, Response<OffersDatum> response) {

                                if (response.isSuccessful()) {
                                    Log.i(MESSAGE_RESPONSE, ": " + response.message());
                                    Toast.makeText(getApplicationContext(), MESSAGE_CREATE_OFFER_SUCCESSFUL, Toast.LENGTH_SHORT).show();

                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("FirstTab", 4);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();

                                } else {
                                    try {
                                        Log.i(MESSAGE_RESPONSE, ": " + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<OffersDatum> call, Throwable t) {
                            }
                        });

                    } else {

                        String date = getDate();
                        String hour;
                        String minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = String.format(FORMAT_TWO_DIGITS, timePickerMeetUpTime.getHour());
                            minute = String.format(FORMAT_TWO_DIGITS, timePickerMeetUpTime.getMinute());
                        } else {
                            hour = String.format(FORMAT_TWO_DIGITS, timePickerMeetUpTime.getCurrentHour());
                            minute = String.format(FORMAT_TWO_DIGITS, timePickerMeetUpTime.getCurrentMinute());
                        }

                        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                        RadioButton radioButtonGender = (RadioButton) findViewById(selectedId);

                        String meetUpTime = date + " " + hour + ":" + minute;
                        OffersDatum offer = new OffersDatum(meetUpTime, editTextStartingLocationName.getText().toString(), textViewStartingLocation.getText().toString(), startingLocationLatitude, startingLocationLongitude, editTextEndingLocationName.getText().toString(), textViewEndingLocation.getText().toString(), endingLocationLatitude, endingLocationLongitude, Integer.parseInt(editTextSeatsAvailable.getText().toString()), editTextRemarks.getText().toString(), 1, radioButtonGender.getText().toString().toLowerCase(), editTextVehiclePlateNumber.getText().toString(), editTextVehicleDescription.getText().toString(), editTextVehicleModel.getText().toString());

                        Call<OffersDatum> call = TerawhereBackendServer.getApiInstance().editOffer(offerId, offer);
                        call.enqueue(new Callback<OffersDatum>() {
                            @Override
                            public void onResponse(Call<OffersDatum> call, Response<OffersDatum> response) {

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
                            public void onFailure(Call<OffersDatum> call, Throwable t) {

                            }
                        });

                    }
                }
            } else {
                Toast.makeText(CreateOfferActivity.this, MESSAGE_NETWORK_ERROR, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }

    private String getMinuteToEdit() {
        String minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            minute = String.format(FORMAT_TWO_DIGITS, timePickerMeetUpTime.getMinute());
        } else {
            minute = String.format(FORMAT_TWO_DIGITS, timePickerMeetUpTime.getCurrentMinute());
        }
        return minute;
    }

    private String getHourToEdit() {
        String hour;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = String.format(FORMAT_TWO_DIGITS, timePickerMeetUpTime.getHour());
        } else {
            hour = String.format(FORMAT_TWO_DIGITS, timePickerMeetUpTime.getCurrentHour());
        }
        return hour;
    }

    private Call<OffersDatum> createOfferApi(OffersDatum offer) {
        return TerawhereBackendServer.getApiInstance().createOffer(offer);
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
        return (startingLocationLatitude == 0.0 || startingLocationLongitude == 0.0 || endingLocationLatitude == 0.0 || endingLocationLongitude == 0.0 || editTextSeatsAvailable.getText().toString().matches("") || editTextRemarks.getText().toString().matches("") || editTextVehicleDescription.getText().toString().matches("") || editTextVehiclePlateNumber.getText().toString().matches(""));

    }

    private void showStartingPlacePickerActivity() throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        initializePlacePickerMap(intentBuilder, startingLocationLatitude, startingLocationLongitude);
        startPlacePickerActivity(intentBuilder, 1);
    }

    private void showEndingPlacePickerActivity() throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        if (isEndingLocationInitialized()) {
            initializePlacePickerMap(intentBuilder, endingLocationLatitude, endingLocationLongitude);
        } else {
            initializePlacePickerMap(intentBuilder, startingLocationLatitude, startingLocationLongitude);
        }
        startPlacePickerActivity(intentBuilder, 2);
    }

    private boolean isEndingLocationInitialized() {
        return (endingLocationLatitude != 0.0 || endingLocationLongitude != 0.0);
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
        TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
        locationTextView.setOnClickListener(this);
    }

    private void endingLocationTextViewListener() {
        TextView endingLocationTextView = (TextView) findViewById(R.id.endingLocationTextView);
        endingLocationTextView.setOnClickListener(this);
    }


    private void createOfferButtonListener() {
        buttonCreateOffer = (Button) findViewById(R.id.createOfferButton);
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
            startingLocationLatitude = lastLocation.getLatitude();
            startingLocationLongitude = lastLocation.getLongitude();
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(startingLocationLatitude, startingLocationLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                if (addresses.size() > 0) {
                    if (addresses.get(0).getAddressLine(0) != null) {
                        String strAddress = MESSAGE_YOU_ARE_AT + addresses.get(0).getAddressLine(0);
                        textViewStartingLocation.setText(strAddress);
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
            startingLocationLatitude = place.getLatLng().latitude;
            startingLocationLongitude = place.getLatLng().longitude;
            textViewStartingLocation.setText(address);
            if (!(place.getName().toString().contains("\"N") || place.getName().toString().contains("\"E") || place.getName().toString().contains("\"S") || place.getName().toString().contains("\"W"))) {
                textViewStartingLocation.append("\n" + place.getName());
            }

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence address = place.getAddress();
            endingLocationLatitude = place.getLatLng().latitude;
            endingLocationLongitude = place.getLatLng().longitude;
            textViewEndingLocation.setText(address);
            if (!(place.getName().toString().contains("\"N") || place.getName().toString().contains("\"E") || place.getName().toString().contains("\"S") || place.getName().toString().contains("\"W"))) {
                textViewStartingLocation.append("\n" + place.getName());
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
