package tech.msociety.terawhere.screens.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
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
import com.google.android.gms.location.places.Place;
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

    private boolean isEditOffer = false;

    private Place selectedStartPlace;
    private Place selectedEndPlace;

    private int offerId;

    private Button buttonCreateOffer;

    private TextInputEditText startLocationEditText;
    private TextInputEditText endLocationEditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
        initToolbar(TOOLBAR_TITLE, true);

        trackCurrentLocation();

        createOfferButtonListener();
        createOfferRelativeLayoutListener();
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
            startLocationEditText.setText(startTerawhereLocation.getAddress());
            endLocationEditText.setText(endTerawhereLocation.getAddress());
            startLatitude = startTerawhereLocation.getLatitude();
            endLatitude = endTerawhereLocation.getLatitude();
            startLongitude = startTerawhereLocation.getLongitude();
            endLongitude = startTerawhereLocation.getLongitude();
            startLocationName = startTerawhereLocation.getName();
            endLocationName = endTerawhereLocation.getName();

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
                    int hour = 19;
                    int minute = 30;
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
        endLocationEditText = (TextInputEditText) findViewById(R.id.edit_text_end_location);
    }

    private void initializeStartingLocationTextView() {
        startLocationEditText = (TextInputEditText) findViewById(R.id.edit_text_start_location);
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

        if (view.getId() == R.id.linearLayout) {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        if (view.getId() == R.id.edit_text_start_location || view.getId() == R.id.text_input_layout_start_location) {
            try {
                showStartingPlacePickerActivity();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.edit_text_end_location || view.getId() == R.id.text_input_layout_end_location) {
            try {
                showEndingPlacePickerActivity();
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
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

    private void showStartingPlacePickerActivity() throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        if (selectedStartPlace != null) {
            initializePlacePickerMap(intentBuilder, selectedStartPlace.getLatLng().latitude, selectedStartPlace.getLatLng().longitude);
        }
        startPlacePickerActivity(intentBuilder, 1);
    }

    private void showEndingPlacePickerActivity() throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        if (selectedEndPlace != null) {
            initializePlacePickerMap(intentBuilder, selectedEndPlace.getLatLng().latitude, selectedEndPlace.getLatLng().longitude);
        }
        startPlacePickerActivity(intentBuilder, 2);
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
            String name = place.getName().toString();
            if (name.contains("\"N") || name.contains("\"E") || name.contains("\"S") || name.contains("\"W")) {
                name = place.getAddress().toString();
            }
            endLocationEditText.setText(getPlaceName(place));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getPlaceName(Place place) {
        String name = null;
        if (place != null) {
            name = place.getName().toString();
            if (name.contains("\"N") || name.contains("\"E") || name.contains("\"S") || name.contains("\"W")) {
                name = place.getAddress().toString();
            }
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
