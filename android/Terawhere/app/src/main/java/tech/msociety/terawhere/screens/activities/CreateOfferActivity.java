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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.offers.OfferRequestBody;
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
    private GoogleApiClient googleApiClient;
    private String MESSAGE_LOCATION_PERMISSION_NEEDED = "Need location";
    private double startLatitude;
    private double endLatitude;
    private double startLongitude;
    private double endLongitude;
    private String startLocationName;
    private String endLocationName;
    private double latitude;
    private double longitude;
    
    private Button buttonCreateOffer;
    private TextInputEditText textInputEditTextVehicleColor;
    private TextInputEditText textInputEditTextEndLocation;
    private TextInputEditText textInputEditTextSeatsAvailable;
    private TextInputEditText textInputEditTextRemarks;
    private TextInputEditText textInputEditTextVehiclePlateNumber;
    private TextInputEditText textInputEditTextVehicleModel;
    private TextInputEditText textInputEditTextMeetUpTime;
    private TextInputEditText textInputEditTextStartLocation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
    
        Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();
    
        initToolbar(TOOLBAR_TITLE, true);
        trackCurrentLocation();
        initViewHandles();
        setClickListeners();
    
        if (bundle != null) {
            isEditOffer = intent.getExtras().getBoolean(IS_EDIT);
        }
        
        if (isEditOffer) {
            offerId = intent.getExtras().getInt(OFFER_ID);
            textInputEditTextSeatsAvailable.setText(String.format(Locale.getDefault(), "%d", intent.getExtras().getInt(SEATS_AVAILABLE)));
            textInputEditTextRemarks.setText(intent.getStringExtra(DRIVER_REMARKS));
    
            Offer offer = intent.getParcelableExtra("offer");
    
            textInputEditTextVehicleColor.setText(offer.getVehicle().getDescription());
            textInputEditTextVehicleModel.setText(offer.getVehicle().getModel());
            textInputEditTextVehiclePlateNumber.setText(offer.getVehicle().getPlateNumber());
            textInputEditTextStartLocation.setText(offer.getStartTerawhereLocation().getName());
            textInputEditTextEndLocation.setText(offer.getEndTerawhereLocation().getName());
    
            startLatitude = offer.getStartTerawhereLocation().getLatitude();
            endLatitude = offer.getEndTerawhereLocation().getLatitude();
            startLongitude = offer.getStartTerawhereLocation().getLongitude();
            endLongitude = offer.getEndTerawhereLocation().getLongitude();
            startLocationName = offer.getStartTerawhereLocation().getName();
            endLocationName = offer.getEndTerawhereLocation().getName();
            
            final Date meetUpTime = offer.getMeetupTime();
            textInputEditTextMeetUpTime.setText(DateUtils.toFriendlyTimeString(meetUpTime));
            buttonCreateOffer.setText(EDIT_OFFER);
            setMeetUpTimeEditTextListener(meetUpTime);
        } else {
            setMeetUpTimeEditTextListener();
        }
    }
    
    private void initViewHandles() {
        buttonCreateOffer = (Button) findViewById(R.id.button_create_offer);
        textInputEditTextMeetUpTime = (TextInputEditText) findViewById(R.id.edit_text_meet_up_time);
        textInputEditTextStartLocation = (TextInputEditText) findViewById(R.id.edit_text_start_location);
        textInputEditTextEndLocation = (TextInputEditText) findViewById(R.id.edit_text_end_location);
        textInputEditTextSeatsAvailable = (TextInputEditText) findViewById(R.id.edit_text_seats_available);
        textInputEditTextRemarks = (TextInputEditText) findViewById(R.id.edit_text_remarks);
        textInputEditTextVehicleModel = (TextInputEditText) findViewById(R.id.edit_text_vehicle_model);
        textInputEditTextVehiclePlateNumber = (TextInputEditText) findViewById(R.id.edit_text_vehicle_number);
        textInputEditTextVehicleColor = (TextInputEditText) findViewById(R.id.text_input_edit_text_vehicle_color);
    }
    
    private void setClickListeners() {
        textInputEditTextStartLocation.setOnClickListener(this);
        textInputEditTextEndLocation.setOnClickListener(this);
        buttonCreateOffer.setOnClickListener(this);
    }
    
    private void setMeetUpTimeEditTextListener() {
        textInputEditTextMeetUpTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(CreateOfferActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        setTextMeetUpTimeEditText(selectedHour, selectedMinute);
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
                timePickerDialog.setTitle(SELECT_TIME);
                showTpdMeetUpTime(timePickerDialog);
            }
        });
    }
    
    private void setMeetUpTimeEditTextListener(final Date meetUpTime) {
        textInputEditTextMeetUpTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpdMeetUpTime;
                tpdMeetUpTime = new TimePickerDialog(CreateOfferActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        setTextMeetUpTimeEditText(selectedHour, selectedMinute);
                    }
                }, meetUpTime.getHours(), meetUpTime.getMinutes(), false);
                tpdMeetUpTime.setTitle(SELECT_TIME);
                showTpdMeetUpTime(tpdMeetUpTime);
            }
        });
    }
    
    private void showTpdMeetUpTime(TimePickerDialog tpdMeetUpTime) {
        tpdMeetUpTime.show();
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
        
        textInputEditTextMeetUpTime.setText(selectedHour + ":" + mm_precede + selectedMinute + AM_PM);
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
            callStartPlaceAutocompleteActivityIntent();
        } else if (view.getId() == R.id.edit_text_end_location || view.getId() == R.id.text_input_layout_end_location) {
            callEndPlaceAutocompleteActivityIntent();
        } else if (view.getId() == R.id.button_create_offer) {
            if (areNotAllFieldsFilled() || !textInputEditTextVehiclePlateNumber.getText().toString().matches("^[a-zA-Z0-9]*$")) {
                Toast.makeText(CreateOfferActivity.this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            } else {
                if (!isEditOffer) {
                    String date = getDate(); // get todays date
                    String time = textInputEditTextMeetUpTime.getText().toString();
                    
                    final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    Date dateObj = null;
                    try {
                        dateObj = sdf.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String meetUpTime = date + " " + new SimpleDateFormat("HH:mm:ss").format(dateObj);
                    OfferRequestBody offerRequestBody = new OfferRequestBody(meetUpTime, getPlaceName(selectedStartPlace),
                            selectedStartPlace.getAddress().toString(), selectedStartPlace.getLatLng().latitude,
                            selectedStartPlace.getLatLng().longitude, getPlaceName(selectedEndPlace), selectedEndPlace.getAddress().toString(),
                            selectedEndPlace.getLatLng().latitude, selectedEndPlace.getLatLng().longitude, Integer.parseInt(textInputEditTextSeatsAvailable.getText().toString()),
                            textInputEditTextRemarks.getText().toString(), textInputEditTextVehiclePlateNumber.getText().toString(),
                            textInputEditTextVehicleColor.getText().toString(), textInputEditTextVehicleModel.getText().toString());
    
                    Call<Void> call = TerawhereBackendServer.getApiInstance().createOffer(offerRequestBody);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
    
                            if (response.isSuccessful()) {
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
                    String date = getDate();
                    String time = textInputEditTextMeetUpTime.getText().toString();
                    
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
    
                    OfferRequestBody offerRequestBody = new OfferRequestBody(meetUpTime,
                            startName,
                            textInputEditTextStartLocation.getText().toString(),
                            getStartLatitude(selectedStartPlace),
                            getStartLongitude(selectedStartPlace),
                            endName,
                            textInputEditTextEndLocation.getText().toString(),
                            getEndLatitude(selectedEndPlace),
                            getEndLongitude(selectedEndPlace),
                            Integer.parseInt(textInputEditTextSeatsAvailable.getText().toString()),
                            textInputEditTextRemarks.getText().toString(),
                            textInputEditTextVehiclePlateNumber.getText().toString(),
                            textInputEditTextVehicleColor.getText().toString(),
                            textInputEditTextVehicleModel.getText().toString());
    
                    Call<Void> call = TerawhereBackendServer.getApiInstance().editOffer(offerId, offerRequestBody);
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
        
                            }
                        }
    
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                        }
                    });
                }
            }
        }
    }
    
    private String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }
    
    private boolean areNotAllFieldsFilled() {
        return (textInputEditTextStartLocation.getText().toString().matches("") || textInputEditTextEndLocation.getText().toString().matches("") || textInputEditTextSeatsAvailable.getText().toString().matches("") || textInputEditTextVehicleColor.getText().toString().matches("") || textInputEditTextVehiclePlateNumber.getText().toString().matches(""));
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
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(autocompleteFilter)
                    .setBoundsBias(placePickerMapBounds)
                    .build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "callStartPlaceAutocompleteActivityIntent: ", e);
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
            Log.e(TAG, "callEndPlaceAutocompleteActivityIntent: ", e);
        }
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
            textInputEditTextStartLocation.setText(getPlaceName(place));
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            selectedEndPlace = place;
            textInputEditTextEndLocation.setText(getPlaceName(place));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    private String getPlaceName(Place place) {
        String name = null;
        if (place != null) {
            name = place.getName().toString();
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