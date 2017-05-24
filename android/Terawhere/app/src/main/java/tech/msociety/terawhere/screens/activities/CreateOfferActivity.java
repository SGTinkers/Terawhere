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
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

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
    public static final String INTENT_OFFER = "intentOffer";
    public static final String INTENT_IS_EDIT = "isEdit";
    public static final String INTENT_IS_CREATE = "isCreate";
    private static final String TOOLBAR_TITLE = "Create Offer";
    public static final double OFFSET_LATITUDE = 0.000225;
    public static final double OFFSET_LONGITUDE = 0.0043705;
    public static final String SELECT_TIME = "Select Time";
    private boolean isEditOffer = false;
    private boolean isCreateOffer = false;
    private Place selectedStartPlace;
    private Place selectedEndPlace;
    private int offerId;
    private double startLatitude;
    private double endLatitude;
    private double startLongitude;
    private double endLongitude;
    private String startLocationName;
    private String endLocationName;
    private String startLocationAddress;
    private String endLocationAddress;
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
        
        initToolbar(TOOLBAR_TITLE, true);
        trackCurrentLocation();
        initViewHandles();
        setClickListeners();
    
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            isEditOffer = intent.getExtras().getBoolean(INTENT_IS_EDIT);
            isCreateOffer = intent.getExtras().getBoolean(INTENT_IS_CREATE);
        }
        
        if (isEditOffer) {
            Offer offer = intent.getParcelableExtra(CreateOfferActivity.INTENT_OFFER);
            unloadOfferIntoUi(offer);
            
            offerId = offer.getOfferId();
            buttonCreateOffer.setText(R.string.create_offer_activity_button_text);
        } else if (isCreateOffer) {
            Offer offer = intent.getParcelableExtra(CreateOfferActivity.INTENT_OFFER);
            unloadOfferIntoUi(offer);
        } else {
            setMeetUpTimeEditTextListener(null);
        }
    }
    
    private void unloadOfferIntoUi(Offer offer) {
        textInputEditTextSeatsAvailable.setText(String.format(Locale.getDefault(), "%d", offer.getVacancy()));
        textInputEditTextRemarks.setText(offer.getRemarks());
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
        startLocationAddress = offer.getStartTerawhereLocation().getAddress();
        endLocationAddress = offer.getEndTerawhereLocation().getAddress();
        
        final Date meetupTime = offer.getMeetupTime();
        textInputEditTextMeetUpTime.setText(DateUtils.toFriendlyTimeString(meetupTime));
        setMeetUpTimeEditTextListener(meetupTime);
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
    
    private void setMeetUpTimeEditTextListener(Date meetupTime) {
        if (meetupTime == null) {
            meetupTime = new Date();
        }
        
        final Calendar calendar = DateUtils.toCalendar(meetupTime);
        
        textInputEditTextMeetUpTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(CreateOfferActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        textInputEditTextMeetUpTime.setText(DateUtils.toFriendlyTimeString(calendar.getTime()));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.setTitle(SELECT_TIME);
                timePickerDialog.show();
            }
        });
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
                if (isCreateOffer) {
                    String dateString = DateUtils.toString(new Date(), DateUtils.MYSQL_DATE_FORMAT);
                    String timeString = textInputEditTextMeetUpTime.getText().toString();
                    
                    final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    Date date = null;
                    try {
                        date = sdf.parse(timeString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String meetupTime = dateString + " " + DateUtils.toString(date, DateUtils.MYSQL_TIME_FORMAT);
                    String startName, endName, startAddress, endAddress;
    
                    if (selectedStartPlace == null) {
                        startName = startLocationName;
                        startAddress = startLocationAddress;
                    } else {
                        startName = getPlaceName(selectedStartPlace);
                        startAddress = getPlaceAddress(selectedStartPlace);
                    }
                    if (selectedEndPlace == null) {
                        endName = endLocationName;
                        endAddress = endLocationAddress;
    
                    } else {
                        endName = getPlaceName(selectedEndPlace);
                        endAddress = getPlaceAddress(selectedEndPlace);
    
                    }
    
                    OfferRequestBody offerRequestBody = new OfferRequestBody(meetupTime,
                            startName,
                            startAddress,
                            getStartLatitude(selectedStartPlace),
                            getStartLongitude(selectedStartPlace),
                            endName,
                            endAddress,
                            getEndLatitude(selectedEndPlace),
                            getEndLongitude(selectedEndPlace),
                            Integer.parseInt(textInputEditTextSeatsAvailable.getText().toString()),
                            textInputEditTextRemarks.getText().toString(),
                            textInputEditTextVehiclePlateNumber.getText().toString(),
                            textInputEditTextVehicleColor.getText().toString(),
                            textInputEditTextVehicleModel.getText().toString());
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
                            }
                        }
    
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
        
                        }
                    });
    
                } else if (isEditOffer) {
                    String dateString = DateUtils.toString(new Date(), DateUtils.MYSQL_DATE_FORMAT);
                    String time = textInputEditTextMeetUpTime.getText().toString();
    
                    final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    Date date = null;
                    try {
                        date = sdf.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String meetUpTime = dateString + " " + DateUtils.toString(date, DateUtils.MYSQL_TIME_FORMAT);
                    
                    String startName, endName, startAddress, endAddress;
    
                    if (selectedStartPlace == null) {
                        startName = startLocationName;
                        startAddress = startLocationAddress;
                    } else {
                        startName = getPlaceName(selectedStartPlace);
                        startAddress = getPlaceAddress(selectedStartPlace);
        
                    }
                    if (selectedEndPlace == null) {
                        endName = endLocationName;
                        endAddress = endLocationAddress;
    
                    } else {
                        endName = getPlaceName(selectedEndPlace);
                        endAddress = getPlaceAddress(selectedEndPlace);
    
                    }
    
                    OfferRequestBody offerRequestBody = new OfferRequestBody(meetUpTime,
                            startName,
                            startAddress,
                            getStartLatitude(selectedStartPlace),
                            getStartLongitude(selectedStartPlace),
                            endName,
                            endAddress,
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
                } else {
                    String dateString = DateUtils.toString(new Date(), DateUtils.MYSQL_DATE_FORMAT);
                    String time = textInputEditTextMeetUpTime.getText().toString();
    
                    final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    Date date = null;
                    try {
                        date = sdf.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String meetUpTime = dateString + " " + DateUtils.toString(date, DateUtils.MYSQL_TIME_FORMAT);
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
                    Toast.makeText(this, "Need location", Toast.LENGTH_SHORT).show();
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
    
    private String getPlaceAddress(Place place) {
        String address = null;
        if (place != null) {
            address = place.getAddress().toString();
        }
        return address;
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