package tech.msociety.terawhere.screens.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.offers.PostOffers;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.abstracts.ToolbarActivity;
import tech.msociety.terawhere.utils.DateUtils;

public class CreateOfferActivity extends ToolbarActivity implements View.OnClickListener {

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
    public static final String SELECT_TIME = "Select Time";

    private boolean isEditOffer = false;

    private Place selectedStartPlace;
    private Place selectedEndPlace;

    private int offerId;

    private Button buttonCreateOffer;

    private EditText startLocationEditText;
    private EditText endLocationEditText;
    private AutoCompleteTextView editTextVehicleDescription;

    private EditText editTextSeatsAvailable;
    private EditText editTextRemarks;
    private EditText editTextVehiclePlateNumber;

    private EditText editTextVehicleModel;
    private EditText editTextMeetUpTime;
    private GoogleApiClient googleApiClient;
    private String MESSAGE_LOCATION_PERMISSION_NEEDED = "Need location";

    double startLatitude, endLatitude, startLongitude, endLongitude;
    String startLocationName, endLocationName;

    double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
        Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();

        initToolbar(TOOLBAR_TITLE, true);

        trackCurrentLocation();

        // set listener
        createOfferButtonListener();
        createOfferRelativeLayoutListener();
        startingLocationTextViewListener();
        endingLocationTextViewListener();

        // initialization
        initializeMeetUpTimeEditText();
        initializeStartingLocationTextView();
        initializeEndingLocationTextView();
        initializeSeatsAvailableEditText();
        initializeRemarksEditText();
        initialializeVehicleDescriptionEditText(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.colors_array)));
        initializeVehicleModelEditText();
        initializeVehicleNumberEditText();
        initializeIsEditOffer(bundle, intent);

        if (isEditOffer) {
            initializeOfferId(intent);
            setSeatsAvailableField(intent);
            setRemarksField(intent);

            // set vehicle details
            Vehicle vehicle = getVehicle(intent);
            setTextVehicleDescriptionEditText(vehicle);
            setTextVehicleModelEditText(vehicle);
            setTextVehiclePlateNumber(vehicle);

            //  set location details
            TerawhereLocation startTerawhereLocation = getStartTerawhereLocation(intent);
            TerawhereLocation endTerawhereLocation = getEndTerawhereLocation(intent);
            setTextStartLocationEditText(startTerawhereLocation);
            setTextEndLocationEditText(endTerawhereLocation);

            // initialize location details
            setStartLatitude(startTerawhereLocation);
            setEndLatitude(endTerawhereLocation);
            setStartLongitude(startTerawhereLocation);
            setEndLongitude(startTerawhereLocation);
            setStartLocationName(startTerawhereLocation);
            setEndLocationName(endTerawhereLocation);

            // set meet up time
            final Date meetUpTime = getMeetUpTime(intent);
            setTextMeetUpTimeEditText(meetUpTime);

            //set button
            setCreateOfferButton();

            // set listener
            setMeetUpTimeEditTextListener(meetUpTime);

        } else {
            setMeetUpTimeEditTextListener();
        }
    }

    private void setMeetUpTimeEditTextListener() {
        editTextMeetUpTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerDialog tpdMeetUpTime = getTimePickerDialog();
                showTpdMeetUpTime(tpdMeetUpTime);

            }
        });
    }

    private void setMeetUpTimeEditTextListener(final Date meetUpTime) {
        editTextMeetUpTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerDialog tpdMeetUpTime = getTimePickerDialog(meetUpTime);
                showTpdMeetUpTime(tpdMeetUpTime);
            }
        });
    }

    private void setTextMeetUpTimeEditText(Date meetUpTime) {
        editTextMeetUpTime.setText(DateUtils.toFriendlyTimeString(meetUpTime));
    }

    private Date getMeetUpTime(Intent intent) {
        return (Date) intent.getSerializableExtra("meetUpTime");
    }

    private void setEndLocationName(TerawhereLocation endTerawhereLocation) {
        endLocationName = endTerawhereLocation.getName();
    }

    private void setStartLocationName(TerawhereLocation startTerawhereLocation) {
        startLocationName = startTerawhereLocation.getName();
    }

    private void setEndLongitude(TerawhereLocation startTerawhereLocation) {
        endLongitude = startTerawhereLocation.getLongitude();
    }

    private void setStartLongitude(TerawhereLocation startTerawhereLocation) {
        startLongitude = startTerawhereLocation.getLongitude();
    }

    private void setEndLatitude(TerawhereLocation endTerawhereLocation) {
        endLatitude = endTerawhereLocation.getLatitude();
    }

    private void setStartLatitude(TerawhereLocation startTerawhereLocation) {
        startLatitude = startTerawhereLocation.getLatitude();
    }

    private void setTextEndLocationEditText(TerawhereLocation endTerawhereLocation) {
        endLocationEditText.setText(endTerawhereLocation.getAddress());
    }

    private void setTextStartLocationEditText(TerawhereLocation startTerawhereLocation) {
        startLocationEditText.setText(startTerawhereLocation.getAddress());
    }

    private TerawhereLocation getEndTerawhereLocation(Intent intent) {
        return intent.getParcelableExtra("endTerawhereLocation");
    }

    private TerawhereLocation getStartTerawhereLocation(Intent intent) {
        return intent.getParcelableExtra("startTerawhereLocation");
    }

    private void setTextVehiclePlateNumber(Vehicle vehicle) {
        editTextVehiclePlateNumber.setText(vehicle.getPlateNumber());
    }

    private void setTextVehicleModelEditText(Vehicle vehicle) {
        editTextVehicleModel.setText(vehicle.getModel());
    }

    private void setTextVehicleDescriptionEditText(Vehicle vehicle) {
        editTextVehicleDescription.setText(vehicle.getDescription());
    }

    private Vehicle getVehicle(Intent intent) {
        return intent.getParcelableExtra("vehicle");
    }

    @NonNull
    private TimePickerDialog getTimePickerDialog() {
        TimePickerDialog tpdMeetUpTime;
        tpdMeetUpTime = new TimePickerDialog(CreateOfferActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                setTextMeetUpTimeEditText(selectedHour, selectedMinute);

            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
        tpdMeetUpTime.setTitle(SELECT_TIME);
        return tpdMeetUpTime;
    }

    private void showTpdMeetUpTime(TimePickerDialog tpdMeetUpTime) {
        tpdMeetUpTime.show();
    }

    @NonNull
    private TimePickerDialog getTimePickerDialog(Date meetUpTime) {
        TimePickerDialog tpdMeetUpTime;
        tpdMeetUpTime = new TimePickerDialog(CreateOfferActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                setTextMeetUpTimeEditText(selectedHour, selectedMinute);

            }
        }, meetUpTime.getHours(), meetUpTime.getMinutes(), false);
        tpdMeetUpTime.setTitle(SELECT_TIME);
        return tpdMeetUpTime;
    }

    private void setTextMeetUpTimeEditText(int selectedHour, int selectedMinute) {
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

    private void initializeVehicleModelEditText() {
        editTextVehicleModel = (EditText) findViewById(R.id.edit_text_vehicle_model);
    }


    private void initialializeVehicleDescriptionEditText(ArrayAdapter<String> adapter) {
        editTextVehicleDescription = (AutoCompleteTextView) findViewById(R.id.edit_text_vehicle_color);
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
        endLocationEditText = (EditText) findViewById(R.id.edit_text_end_location);
    }

    private void initializeStartingLocationTextView() {
        startLocationEditText = (EditText) findViewById(R.id.edit_text_start_location);
    }

    private void initializeMeetUpTimeEditText() {
        editTextMeetUpTime = (EditText) findViewById(R.id.edit_text_meet_up_time);


    }

    private void trackCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Location location = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.linearLayout) {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        if (view.getId() == R.id.edit_text_start_location || view.getId() == R.id.text_input_layout_start_location) {
            /*try {
                //showStartingPlacePickerActivity();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }*/
            callStartPlaceAutocompleteActivityIntent();
        } else if (view.getId() == R.id.edit_text_end_location || view.getId() == R.id.text_input_layout_end_location) {
            /*try {
                showEndingPlacePickerActivity();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }*/
            callEndPlaceAutocompleteActivityIntent();

        } else if (view.getId() == R.id.button_create_offer) {

            if (areNotAllFieldsFilled() || !editTextVehiclePlateNumber.getText().toString().matches("^[a-zA-Z0-9]*$")) {
                Log.i("meetUpTime", " : " + editTextMeetUpTime.getText());

                Log.i("StartingAddress", ":" + startLocationEditText.getText().toString());
                if (selectedStartPlace == null) {
                    Log.i("StartingName", ":" + startLocationName);

                    Log.i("StartingLatitude", ":" + startLatitude);
                    Log.i("StartingLongitude", ":" + startLongitude);
                } else {
                    Log.i("StartingName", ":" + getPlaceName(selectedStartPlace));

                    Log.i("StartingLatitude", ":" + selectedStartPlace.getLatLng().latitude);
                    Log.i("StartingLongitude", ":" + selectedStartPlace.getLatLng().longitude);
                }
                Log.i("EndingAddress", ":" + endLocationEditText.getText().toString());
                if (selectedEndPlace == null) {
                    Log.i("EndingLocationName", ":" + endLocationName);

                    Log.i("EndingLatitue", ":" + endLatitude);
                    Log.i("EndingLongitude", ":" + endLongitude);
                } else {
                    Log.i("EndingLocationName", ":" + getPlaceName(selectedEndPlace));

                    Log.i("EndingLatitue", ":" + selectedEndPlace.getLatLng().latitude);
                    Log.i("EndingLongitude", ":" + selectedEndPlace.getLatLng().longitude);
                }
                Log.i("SeatsAvailable", ":" + Integer.parseInt(editTextSeatsAvailable.getText().toString()));
                Log.i("Remarks", ":" + editTextRemarks.getText().toString());
                Log.i("VehiclePlateNumber", ":" + editTextVehiclePlateNumber.getText().toString());
                Log.i("VehicleDesc", ":" + editTextVehicleDescription.getText().toString());
                Log.i("VehicleModel", ":" + editTextVehicleModel.getText().toString());
                Toast.makeText(CreateOfferActivity.this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
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
                    PostOffers postOffers = new PostOffers(meetUpTime, getPlaceName(selectedStartPlace),
                            selectedStartPlace.getAddress().toString(), selectedStartPlace.getLatLng().latitude,
                            selectedStartPlace.getLatLng().longitude, getPlaceName(selectedEndPlace), selectedEndPlace.getAddress().toString(),
                            selectedEndPlace.getLatLng().latitude, selectedEndPlace.getLatLng().longitude, Integer.parseInt(editTextSeatsAvailable.getText().toString()),
                            editTextRemarks.getText().toString(), editTextVehiclePlateNumber.getText().toString(),
                            editTextVehicleDescription.getText().toString(), editTextVehicleModel.getText().toString());

                    Log.i("meetUpTime", " : " + meetUpTime);
                    Log.i("StartingName", ":" + getPlaceName(selectedStartPlace));
                    Log.i("StartingAddress", ":" + startLocationEditText.getText().toString());
                    Log.i("StartingLatitude", ":" + selectedStartPlace.getLatLng().latitude);
                    Log.i("StartingLongitude", ":" + selectedStartPlace.getLatLng().longitude);
                    Log.i("EndingName", ":" + getPlaceName(selectedEndPlace));
                    Log.i("EndingAddress", ":" + endLocationEditText.getText().toString());
                    Log.i("EndingLatitue", ":" + selectedEndPlace.getLatLng().latitude);
                    Log.i("EndingLongitude", ":" + selectedEndPlace.getLatLng().longitude);
                    Log.i("SeatsAvailable", ":" + Integer.parseInt(editTextSeatsAvailable.getText().toString()));
                    Log.i("Remarks", ":" + editTextRemarks.getText().toString());
                    Log.i("VehiclePlateNumber", ":" + editTextVehiclePlateNumber.getText().toString());
                    Log.i("VehicleDesc", ":" + editTextVehicleDescription.getText().toString());
                    Log.i("VehicleModel", ":" + editTextVehicleModel.getText().toString());

                    Call<Void> call = createOfferApi(postOffers);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            if (response.isSuccessful()) {
                                Log.i(LOG_RESPONSE, ": " + response.message());
                                //Toast.makeText(getApplicationContext(), MESSAGE_CREATE_OFFER_SUCCESSFUL, Toast.LENGTH_SHORT).show();
                                final Dialog successDialog = new Dialog(CreateOfferActivity.this);
                                successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                successDialog.setContentView(R.layout.dialog_offer_successful);
                                successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                successDialog.setCanceledOnTouchOutside(false);
                                successDialog.setCancelable(false);

                                Button okButton = (Button) successDialog.findViewById(R.id.button_ok);
                                TextView dialogInfo = (TextView) successDialog.findViewById(R.id.text_view_successfully_created);
                                dialogInfo.setText("Your offer has been successfully created");
                                okButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        successDialog.dismiss();

                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("FirstTab", 4);
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    }
                                });
                                successDialog.show();


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

                    String startName, endName;

                    if (selectedStartPlace == null) {
                        startName = startLocationName;
                    } else {
                        startName = getPlaceName(selectedStartPlace);

                    }
                    if (selectedEndPlace == null) {
                        endName = endLocationName;
                    } else {
                        endName = getPlaceName(selectedEndPlace);

                    }


                    PostOffers postOffers = new PostOffers(meetUpTime, startName,
                            startLocationEditText.getText().toString(), getStartLatitude(selectedStartPlace), getStartLongitude(selectedStartPlace),
                            endName, endLocationEditText.getText().toString(),
                            getEndLatitude(selectedEndPlace), getEndLongitude(selectedEndPlace),
                            Integer.parseInt(editTextSeatsAvailable.getText().toString()),
                            editTextRemarks.getText().toString(),
                            editTextVehiclePlateNumber.getText().toString(), editTextVehicleDescription.getText().toString(),
                            editTextVehicleModel.getText().toString());

                    Call<Void> call = TerawhereBackendServer.getApiInstance().editOffer(offerId, postOffers);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            if (response.isSuccessful()) {
                                Log.i("EDIT_MESSAGE", ": " + response.message());
                                final Dialog successDialog = new Dialog(CreateOfferActivity.this);
                                successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                successDialog.setContentView(R.layout.dialog_offer_successful);
                                successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                successDialog.setCanceledOnTouchOutside(false);
                                successDialog.setCancelable(false);

                                Button okButton = (Button) successDialog.findViewById(R.id.button_ok);
                                TextView dialogInfo = (TextView) successDialog.findViewById(R.id.text_view_successfully_created);
                                dialogInfo.setText("Your offer has been successfully updated");

                                okButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        successDialog.dismiss();

                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("FirstTab", 4);
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    }
                                });
                                successDialog.show();


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
        return (startLocationEditText.getText().toString().matches("") || endLocationEditText.getText().toString().matches("") || editTextSeatsAvailable.getText().toString().matches("") || editTextVehicleDescription.getText().toString().matches("") || editTextVehiclePlateNumber.getText().toString().matches(""));

    }

    /*private void showStartingPlacePickerActivity() throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        if (selectedStartPlace != null) {
            initializePlacePickerMap(intentBuilder, selectedStartPlace.getLatLng().latitude, selectedStartPlace.getLatLng().longitude);
        } else {
            initializePlacePickerMap(intentBuilder, latitude, longitude);
        }
        startPlacePickerActivity(intentBuilder, 1);
    }

    private void showEndingPlacePickerActivity() throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        if (selectedEndPlace != null) {
            initializePlacePickerMap(intentBuilder, selectedEndPlace.getLatLng().latitude, selectedEndPlace.getLatLng().longitude);
        } else {
            initializePlacePickerMap(intentBuilder, latitude, longitude);
        }
        startPlacePickerActivity(intentBuilder, 2);
    }*/

    /*private void initializePlacePickerMap(PlacePicker.IntentBuilder intentBuilder, double locationLatitude, double locationLongitude) {
        LatLng minimumBound = new LatLng(locationLatitude - OFFSET_LATITUDE, locationLongitude - OFFSET_LONGITUDE);
        LatLng maximumBound = new LatLng(locationLatitude + OFFSET_LATITUDE, locationLongitude + OFFSET_LONGITUDE);
        LatLngBounds placePickerMapBounds = new LatLngBounds(minimumBound, maximumBound);
        intentBuilder.setLatLngBounds(placePickerMapBounds);
    }

    private void startPlacePickerActivity(PlacePicker.IntentBuilder intentBuilder, int requestCode) throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        Intent intent = intentBuilder.build(CreateOfferActivity.this);
        startActivityForResult(intent, requestCode);
    }*/

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void callStartPlaceAutocompleteActivityIntent() {
        LatLng minimumBound = new LatLng(latitude - OFFSET_LATITUDE, longitude - OFFSET_LONGITUDE);
        LatLng maximumBound = new LatLng(latitude + OFFSET_LATITUDE, longitude + OFFSET_LONGITUDE);
        LatLngBounds placePickerMapBounds = new LatLngBounds(minimumBound, maximumBound);
        try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("SG")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(autocompleteFilter).setBoundsBias(placePickerMapBounds)
                            .build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {

        }

    }

    private void callEndPlaceAutocompleteActivityIntent() {
        LatLng minimumBound = new LatLng(latitude - OFFSET_LATITUDE, longitude - OFFSET_LONGITUDE);
        LatLng maximumBound = new LatLng(latitude + OFFSET_LATITUDE, longitude + OFFSET_LONGITUDE);
        LatLngBounds placePickerMapBounds = new LatLngBounds(minimumBound, maximumBound);
        try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("SG")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(autocompleteFilter).setBoundsBias(placePickerMapBounds)
                            .build(this);
            startActivityForResult(intent, 2);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {

        }

    }

    /**
     * on click listeners
     */

    private void createOfferRelativeLayoutListener() {
        findViewById(R.id.linearLayout).setOnClickListener(this);
    }

    private void startingLocationTextViewListener() {
        findViewById(R.id.edit_text_start_location).setOnClickListener(this);
        findViewById(R.id.text_input_layout_start_location).setOnClickListener(this);
    }

    private void endingLocationTextViewListener() {
        findViewById(R.id.edit_text_end_location).setOnClickListener(this);
        findViewById(R.id.text_input_layout_end_location).setOnClickListener(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            selectedStartPlace = place;
            startLocationEditText.setText(getPlaceName(place));
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            selectedEndPlace = place;
            endLocationEditText.setText(getPlaceName(place));

            // don't need as of now
            /*String name = place.getName().toString();
            if (name.contains("\"N") || name.contains("\"E") || name.contains("\"S") || name.contains("\"W")) {
                name = place.getAddress().toString();
            }*/
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }

    private String getPlaceName(Place place) {
        String name = null;
        if (place != null) {
            name = place.getName().toString();

            // don't need as of now
            /*if (name.contains("\"N") || name.contains("\"E") || name.contains("\"S") || name.contains("\"W")) {
                name = place.getAddress().toString();
            }*/
        }

        return name;
    }


    private double getStartLatitude(Place place) {
        if (place == null) {
            return startLatitude;
        } else {
            return selectedStartPlace.getLatLng().latitude;
        }
    }

    private double getEndLatitude(Place place) {
        if (place == null) {
            return endLatitude;
        } else {
            return selectedEndPlace.getLatLng().latitude;
        }
    }

    private double getStartLongitude(Place place) {
        if (place == null) {
            return startLongitude;
        } else {
            return selectedStartPlace.getLatLng().longitude;
        }
    }

    private double getEndLongitude(Place place) {
        if (place == null) {
            return endLongitude;
        } else {
            return selectedEndPlace.getLatLng().longitude;
        }
    }
}
