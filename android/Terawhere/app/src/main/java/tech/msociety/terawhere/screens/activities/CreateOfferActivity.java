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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.events.OfferCreatedEvent;
import tech.msociety.terawhere.globals.TerawhereApplication;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.offers.OfferRequestBody;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.abstracts.ToolbarActivity;
import tech.msociety.terawhere.utils.DateUtils;

public class CreateOfferActivity extends ToolbarActivity {
    public static final String INTENT_OFFER = "INTENT_OFFER";
    public static final String INTENT_IS_EDIT = "INTENT_IS_EDIT";
    public static final String INTENT_IS_CREATE = "INTENT_IS_CREATE";
    public static final int REQUEST_CODE_GET_START_PLACE = 1;
    public static final int REQUEST_CODE_GET_END_PLACE = 2;
    private static final String TOOLBAR_TITLE_CREATE_OFFER = "Create Offer";
    private static final String TOOLBAR_TITLE_EDIT_OFFER = "Edit Offer";
    public static final double OFFSET_LATITUDE = 0.000225;
    public static final double OFFSET_LONGITUDE = 0.0043705;
    public static final String SELECT_TIME = "Select Time";
    private boolean isEditOffer = false;
    private boolean isCreateOffer = false;
    private Location currentLocation;
    private Place placeStart;
    private Place placeEnd;
    private Offer offer = null;

    private Button buttonCreateOffer;
    private TextInputEditText textInputEditTextVehicleColor;
    private TextInputEditText textInputEditTextEndLocation;
    private TextInputEditText textInputEditTextSeatsAvailable;
    private TextInputEditText textInputEditTextRemarks;
    private TextInputEditText textInputEditTextVehiclePlateNumber;
    private TextInputEditText textInputEditTextVehicleModel;
    private TextInputEditText textInputEditTextMeetUpTime;
    private TextInputEditText textInputEditTextStartLocation;

    public static Intent getIntentToStartInCreateMode(Context sourceContext) {
        Intent intent = new Intent(sourceContext, CreateOfferActivity.class);
        intent.putExtra(CreateOfferActivity.INTENT_IS_CREATE, false);
        return intent;
    }

    public static Intent getIntentToStartInCreateModePrepopulated(Context sourceContext, Offer referenceOffer) {
        Intent intent = new Intent(sourceContext, CreateOfferActivity.class);
        intent.putExtra(CreateOfferActivity.INTENT_IS_CREATE, true);
        intent.putExtra(CreateOfferActivity.INTENT_OFFER, referenceOffer);
        return intent;
    }

    public static Intent getIntentToStartInEditMode(Context sourceContext, Offer offerToBeEdited) {
        Intent intent = new Intent(sourceContext, CreateOfferActivity.class);
        intent.putExtra(CreateOfferActivity.INTENT_IS_EDIT, true);
        intent.putExtra(CreateOfferActivity.INTENT_OFFER, offerToBeEdited);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);

        trackCurrentLocation();
        initViewHandles();
        initTimePickerDialog(null);
        setClickListeners();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            isEditOffer = intent.getExtras().getBoolean(INTENT_IS_EDIT, false);
            isCreateOffer = intent.getExtras().getBoolean(INTENT_IS_CREATE, false);
        }

        if (isEditOffer) {
            offer = intent.getParcelableExtra(CreateOfferActivity.INTENT_OFFER);
            unloadOfferIntoUi(offer);
            buttonCreateOffer.setText(R.string.create_offer_activity_button_text);
            initToolbar(TOOLBAR_TITLE_EDIT_OFFER, true);

        } else if (isCreateOffer) {
            offer = intent.getParcelableExtra(CreateOfferActivity.INTENT_OFFER);
            unloadOfferIntoUi(offer);
            initToolbar(TOOLBAR_TITLE_CREATE_OFFER, true);
        } else {
            initToolbar(TOOLBAR_TITLE_CREATE_OFFER, true);
        }

        ((TerawhereApplication) getApplication()).trackEvent("Launched Create Offer Screen");
    }

    private void unloadOfferIntoUi(Offer offer) {
        Date meetupTime = offer.getMeetupTime();
        textInputEditTextMeetUpTime.setText(DateUtils.toFriendlyTimeString(meetupTime));
        initTimePickerDialog(meetupTime);

        textInputEditTextStartLocation.setText(offer.getStartTerawhereLocation().getName());
        textInputEditTextEndLocation.setText(offer.getEndTerawhereLocation().getName());
        textInputEditTextSeatsAvailable.setText(String.format(Locale.getDefault(), "%d", offer.getVacancy()));

        textInputEditTextVehiclePlateNumber.setText(offer.getVehicle().getPlateNumber());
        textInputEditTextVehicleModel.setText(offer.getVehicle().getModel());
        textInputEditTextVehicleColor.setText(offer.getVehicle().getDescription());

        textInputEditTextRemarks.setText(offer.getRemarks());
    }

    private void initViewHandles() {
        buttonCreateOffer = (Button) findViewById(R.id.button_create_offer);
        textInputEditTextMeetUpTime = (TextInputEditText) findViewById(R.id.text_input_edit_text_meetup_time);
        textInputEditTextStartLocation = (TextInputEditText) findViewById(R.id.text_input_edit_text_start_location);
        textInputEditTextEndLocation = (TextInputEditText) findViewById(R.id.text_input_edit_text_end_location);
        textInputEditTextSeatsAvailable = (TextInputEditText) findViewById(R.id.text_input_edit_text_seats_available);
        textInputEditTextRemarks = (TextInputEditText) findViewById(R.id.text_input_edit_text_remarks);
        textInputEditTextVehicleModel = (TextInputEditText) findViewById(R.id.text_input_edit_text_vehicle_model);
        textInputEditTextVehiclePlateNumber = (TextInputEditText) findViewById(R.id.text_input_edit_text_vehicle_number);
        textInputEditTextVehicleColor = (TextInputEditText) findViewById(R.id.text_input_edit_text_vehicle_color);
    }

    private void setClickListeners() {
        textInputEditTextStartLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callStartPlaceAutocompleteActivityIntent();
            }
        });

        textInputEditTextEndLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEndPlaceAutocompleteActivityIntent();
            }
        });

        textInputEditTextStartLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callStartPlacePickerActivityIntent();
                return true;
            }
        });

        textInputEditTextEndLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callEndPlacePickerActivityIntent();
                return true;
            }
        });

        buttonCreateOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFormFilled()) {
                    ((TerawhereApplication) getApplication()).trackEvent("Submitted Offer without filling all fields");
                    Toast.makeText(CreateOfferActivity.this, R.string.message_required_fields, Toast.LENGTH_SHORT).show();
                    return;
                }

                OfferRequestBody offerRequestBody = getOfferRequestBodyFromUi();

                if (isCreateOffer) {
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
                                        EventBus.getDefault().post(new OfferCreatedEvent());
                                        finish();
                                    }
                                });
                                successDialog.show();
                                ((TerawhereApplication) getApplication()).trackEvent("Submit Offer successful");
                            } else {
                                try {
                                    TerawhereBackendServer.ErrorDatum.ParseErrorAndToast(CreateOfferActivity.this, response);
                                    ((TerawhereApplication) getApplication()).trackEvent("Submit Offer encountered error");
                                } catch (IOException e) {
                                    onFailure(call, e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            TerawhereBackendServer.ErrorDatum.ToastUnknownError(CreateOfferActivity.this, t);
                            ((TerawhereApplication) getApplication()).trackEvent("Submit Offer encountered error");
                        }
                    });
                } else if (isEditOffer) {
                    Call<Void> call = TerawhereBackendServer.getApiInstance().editOffer(offer.getOfferId(), offerRequestBody);
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
                                dialogInfo.setText("Your offer has been successfully updated");
                                okButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        successDialog.dismiss();

                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("FirstTab", 4);
                                        setResult(RESULT_OK, resultIntent);
                                        EventBus.getDefault().post(new OfferCreatedEvent());
                                        finish();
                                    }
                                });
                                successDialog.show();
                                ((TerawhereApplication) getApplication()).trackEvent("Submit Offer successful");
                            } else {
                                try {
                                    TerawhereBackendServer.ErrorDatum.ParseErrorAndToast(CreateOfferActivity.this, response);
                                    ((TerawhereApplication) getApplication()).trackEvent("Submit Offer encountered error");
                                } catch (IOException e) {
                                    onFailure(call, e);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            TerawhereBackendServer.ErrorDatum.ToastUnknownError(CreateOfferActivity.this, t);
                            ((TerawhereApplication) getApplication()).trackEvent("Submit Offer encountered error");
                        }
                    });
                } else {
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
                                ((TerawhereApplication) getApplication()).trackEvent("Submit Offer successful");
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                            ((TerawhereApplication) getApplication()).trackEvent("Submit Offer encountered error");
                        }
                    });
                }
            }

            private OfferRequestBody getOfferRequestBodyFromUi() {
                String startName;
                String startAddress;
                double startLatitude;
                double startLongitude;
                String endName;
                String endAddress;
                double endLatitude;
                double endLongitude;

                startName = placeStart != null ? placeStart.getName().toString() : offer.getStartTerawhereLocation().getName();
                startAddress = placeStart != null ? placeStart.getAddress().toString() : offer.getStartTerawhereLocation().getAddress();
                startLatitude = placeStart != null ? placeStart.getLatLng().latitude : offer.getStartTerawhereLocation().getLatitude();
                startLongitude = placeStart != null ? placeStart.getLatLng().longitude : offer.getStartTerawhereLocation().getLongitude();

                endName = placeEnd != null ? placeEnd.getName().toString() : offer.getEndTerawhereLocation().getName();
                endAddress = placeEnd != null ? placeEnd.getAddress().toString() : offer.getEndTerawhereLocation().getAddress();
                endLatitude = placeEnd != null ? placeEnd.getLatLng().latitude : offer.getEndTerawhereLocation().getLatitude();
                endLongitude = placeEnd != null ? placeEnd.getLatLng().longitude : offer.getEndTerawhereLocation().getLongitude();

                Date dateWithTimeComponent = DateUtils.fromFriendlyTimeString(textInputEditTextMeetUpTime.getText().toString());
                Date dateComplete = DateUtils.getDateFromDates(new Date(), dateWithTimeComponent);

                // if the time user select has passed, increment the day
                if (dateComplete.before(new Date())) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(dateComplete);
                    c.add(Calendar.DATE, 1);
                    dateComplete = c.getTime();
                }

                return new OfferRequestBody(
                        DateUtils.dateToString(dateComplete, DateUtils.MYSQL_DATE_TIME_FORMAT, DateUtils.TIMEZONE_UTC),
                        startName,
                        startAddress,
                        startLatitude,
                        startLongitude,
                        endName,
                        endAddress,
                        endLatitude,
                        endLongitude,
                        Integer.parseInt(textInputEditTextSeatsAvailable.getText().toString()),
                        textInputEditTextRemarks.getText().toString(),
                        textInputEditTextVehiclePlateNumber.getText().toString(),
                        textInputEditTextVehicleColor.getText().toString(),
                        textInputEditTextVehicleModel.getText().toString()
                );
            }
        });
    }

    private void initTimePickerDialog(Date meetupTime) {
        if (meetupTime == null) {
            meetupTime = new Date();
        }

        final Calendar calendar = DateUtils.dateToCalendar(meetupTime);

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

                        if (calendar.getTime().before(new Date())) {
                            Toast.makeText(CreateOfferActivity.this, "Cannot select time that is in the past.", Toast.LENGTH_SHORT).show();
                        } else {
                            textInputEditTextMeetUpTime.setText(DateUtils.toFriendlyTimeString(calendar.getTime()));
                        }
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

        currentLocation = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isFormFilled() {
        return !(textInputEditTextStartLocation.getText().toString().matches("") || textInputEditTextEndLocation.getText().toString().matches("") || textInputEditTextSeatsAvailable.getText().toString().matches("") || textInputEditTextVehicleColor.getText().toString().matches("") || textInputEditTextVehiclePlateNumber.getText().toString().matches(""));
    }

    private String getCountryCode() {
        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String countryCode = tm.getNetworkCountryIso();
        if (countryCode == null) {
            countryCode = "SG";
        }

        return countryCode.toUpperCase();
    }
    
    private void callStartPlaceAutocompleteActivityIntent() {
        LatLng minimumBound = new LatLng(currentLocation.getLatitude() - OFFSET_LATITUDE, currentLocation.getLongitude() - OFFSET_LONGITUDE);
        LatLng maximumBound = new LatLng(currentLocation.getLatitude() + OFFSET_LATITUDE, currentLocation.getLongitude() + OFFSET_LONGITUDE);
        LatLngBounds placePickerMapBounds = new LatLngBounds(minimumBound, maximumBound);
        Log.d("Country", getCountryCode());
        try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setCountry(getCountryCode())
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(autocompleteFilter)
                    .setBoundsBias(placePickerMapBounds)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_GET_START_PLACE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "callStartPlaceAutocompleteActivityIntent: ", e);
        }
    }
    
    private void callEndPlaceAutocompleteActivityIntent() {
        LatLng minimumBound = new LatLng(currentLocation.getLatitude() - OFFSET_LATITUDE, currentLocation.getLongitude() - OFFSET_LONGITUDE);
        LatLng maximumBound = new LatLng(currentLocation.getLatitude() + OFFSET_LATITUDE, currentLocation.getLongitude() + OFFSET_LONGITUDE);
        LatLngBounds placePickerMapBounds = new LatLngBounds(minimumBound, maximumBound);
        try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setCountry(getCountryCode())
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(autocompleteFilter).setBoundsBias(placePickerMapBounds)
                            .build(this);
            startActivityForResult(intent, REQUEST_CODE_GET_END_PLACE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "callEndPlaceAutocompleteActivityIntent: ", e);
        }
    }

    private void callStartPlacePickerActivityIntent() {
        LatLng minimumBound = new LatLng(currentLocation.getLatitude() - OFFSET_LATITUDE, currentLocation.getLongitude() - OFFSET_LONGITUDE);
        LatLng maximumBound = new LatLng(currentLocation.getLatitude() + OFFSET_LATITUDE, currentLocation.getLongitude() + OFFSET_LONGITUDE);
        LatLngBounds placePickerMapBounds = new LatLngBounds(minimumBound, maximumBound);
        try {
            Intent intent = new PlacePicker.IntentBuilder()
                    .setLatLngBounds(placePickerMapBounds)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_GET_START_PLACE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "callStartPlacePickerActivityIntent: ", e);
        }
    }

    private void callEndPlacePickerActivityIntent() {
        LatLng minimumBound = new LatLng(currentLocation.getLatitude() - OFFSET_LATITUDE, currentLocation.getLongitude() - OFFSET_LONGITUDE);
        LatLng maximumBound = new LatLng(currentLocation.getLatitude() + OFFSET_LATITUDE, currentLocation.getLongitude() + OFFSET_LONGITUDE);
        LatLngBounds placePickerMapBounds = new LatLngBounds(minimumBound, maximumBound);
        try {
            Intent intent = new PlacePicker.IntentBuilder()
                    .setLatLngBounds(placePickerMapBounds)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_GET_START_PLACE);
            startActivityForResult(intent, REQUEST_CODE_GET_END_PLACE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "callEndPlacePickerActivityIntent: ", e);
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
        if (requestCode == REQUEST_CODE_GET_START_PLACE && resultCode == RESULT_OK) {
            placeStart = PlacePicker.getPlace(this, data);
            textInputEditTextStartLocation.setText(getPlaceNameWithPrefix(placeStart));
        } else if (requestCode == REQUEST_CODE_GET_END_PLACE && resultCode == RESULT_OK) {
            placeEnd = PlacePicker.getPlace(this, data);
            textInputEditTextEndLocation.setText(getPlaceNameWithPrefix(placeEnd));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private CharSequence getPlaceNameWithPrefix(Place place) {
        String name = place.getName().toString();
        if (name.contains("°") && name.contains("'") && name.contains("\"")) {
            return place.getAddress().toString().split(",")[0];
        } else if (place.getPlaceTypes() == null) {
        } else if (place.getPlaceTypes().contains(Place.TYPE_BUS_STATION)) {
            return "Bus Stop @ " + name;
        } else if (place.getPlaceTypes().contains(Place.TYPE_SUBWAY_STATION)) {
            return "MRT Station @ " + name;
        } else if (place.getPlaceTypes().contains(Place.TYPE_TRAIN_STATION)) {
            return "Train Station @ " + name;
        } else if (place.getPlaceTypes().contains(Place.TYPE_TAXI_STAND)) {
            return "Taxi Stand @ " + name;
        } else if (place.getPlaceTypes().contains(Place.TYPE_PARKING)) {
            return "Parking @ " + name;
        } else if (place.getPlaceTypes().contains(Place.TYPE_AIRPORT)) {
            return "Airport @ " + name;
        }

        return place.getName();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}