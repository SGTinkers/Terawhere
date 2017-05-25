package tech.msociety.terawhere.screens.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.adapters.OfferInfoViewAdapter;
import tech.msociety.terawhere.events.BookingCreatedEvent;
import tech.msociety.terawhere.events.OfferCreatedEvent;
import tech.msociety.terawhere.exceptions.NetworkCallFailedException;
import tech.msociety.terawhere.globals.AppPrefs;
import tech.msociety.terawhere.globals.TerawhereApplication;
import tech.msociety.terawhere.maps.ClusterMarkerLocation;
import tech.msociety.terawhere.maps.ClusterRenderer;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.models.factories.OfferFactory;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.bookings.BookingRequestBody;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.offers.GetOffersResponse;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.setlocation.LocationRequestBody;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.fragments.abstracts.BaseFragment;
import tech.msociety.terawhere.utils.AndroidSdkCheckerUtils;
import tech.msociety.terawhere.utils.DateUtils;
import tech.msociety.terawhere.utils.TerawherePermissionCheckerUtils;

public class HomeFragment extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    
    private GoogleApiClient googleApiClient;
    
    private Location location;
    
    private boolean firstLoadInit;
    
    private ViewPager viewPager;
    
    private ClusterManager<ClusterMarkerLocation> clusterManager;

    private GoogleMap googleMap;

    @Override
    protected boolean needsEventBus() {
        return true;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        
        trackCurrentLocation();
        // Faruq: Shouldn't need this as in the BaseActivity we already have a guard for requireLocationServices
        // It is here to pass Android IDE inspection
        if (AndroidSdkCheckerUtils.isMarshmallow()) {
            TerawherePermissionCheckerUtils.checkPermission(getActivity());
        }
        
        buildGoogleApiClient();
        
        initializeSupportMapFragment();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }
    
    private void trackCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        
        this.location = ((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.i("LAT:", ":" + location.getLatitude());
        Log.i("LON:", ":" + location.getLongitude());
        
    }
    
    private void initializeSupportMapFragment() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_container);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    
        if (AndroidSdkCheckerUtils.isMarshmallow()) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            googleMap.setMyLocationEnabled(true);
        }
        
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        
        initClusterManager();
    }
    
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    
    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(60);
        locationRequest.setFastestInterval(10);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }
    
    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }
    
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    
    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        
        // Alternative entry point for data loading
        if (!firstLoadInit) {
            loadMarkers();
            firstLoadInit = true;
        }
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            Toast.makeText(getContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
            loadMarkers();
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void initClusterManager() {
        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarkerLocation>() {
            @Override
            public void onClusterItemInfoWindowClick(ClusterMarkerLocation clusterMarkerLocation) {
                Offer offer = clusterMarkerLocation.getOffer();
                showBookingDialog(offer);
            }
        });
        clusterManager.setRenderer(new ClusterRenderer(getContext(), googleMap, clusterManager));
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusterMarkerLocation>() {
            @Override
            public boolean onClusterClick(Cluster<ClusterMarkerLocation> cluster) {
                ArrayList<String> items = new ArrayList<>();
                final ArrayList<Offer> offers = new ArrayList<>();
                for (ClusterMarkerLocation clusterMarkerLocation : cluster.getItems()) {
                    Offer offer = clusterMarkerLocation.getOffer();
                    String time = DateUtils.toFriendlyTimeString(offer.getMeetupTime());
                    items.add(time + " to " + offer.getEndTerawhereLocation().getName());
                    offers.add(offer);
                }
                new AlertDialog.Builder(getActivity())
                        .setTitle(offers.size() + " Destinations")
                        .setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Offer offer = offers.get(which);
                                showBookingDialog(offer);
                            }
                        })
                        .show();
                return true;
            }
        });
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnInfoWindowClickListener(clusterManager);
        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        googleMap.setOnMarkerClickListener(clusterManager);
    }
    
    private void loadMarkers() {
        Call<GetOffersResponse> callGetOffers = TerawhereBackendServer.getApiInstance().getNearbyOffers(new LocationRequestBody(location.getLatitude(), location.getLongitude()));
        callGetOffers.enqueue(new Callback<GetOffersResponse>() {
            @Override
            public void onResponse(Call<GetOffersResponse> call, Response<GetOffersResponse> response) {
                if (response.isSuccessful()) {
                    googleMap.clear();
                    clusterManager.clearItems();
    
                    GetOffersResponse getOffersResponse = response.body();
                    List<Offer> offers = OfferFactory.createFromResponse(getOffersResponse);

                    for (int i = 0; i < offers.size(); i++) {
                        Offer offer = offers.get(i);
                        LatLng startLatLng = new LatLng(offer.getStartTerawhereLocation().getLatitude(), offer.getStartTerawhereLocation().getLongitude());
    
                        if (offers.get(i).getSeatsRemaining() > 0) {
                            clusterManager.addItem(new ClusterMarkerLocation(offer, startLatLng));
                        } else {
                            offers.remove(i);
                            i--;
                        }
                    }
                    clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new OfferInfoViewAdapter(LayoutInflater.from(getContext())));
    
                    // Zoom in after markers loaded
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    
                    // Update cluster (needed for refresh)
                    clusterManager.cluster();

                    if (offers.size() > 0) {
                        Toast.makeText(getActivity(), offers.size() + " rides available.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "No rides available.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    onFailure(call, new NetworkCallFailedException("Response not successful."));
                }
            }
    
            @Override
            public void onFailure(Call<GetOffersResponse> call, Throwable t) {
                Log.e(TAG, "failed to fetch offers via network call", t);
            }
        });
    }
    
    private Call<Void> createBookingApi(BookingRequestBody booking) {
        return TerawhereBackendServer.getApiInstance().createBooking(booking);
    }
    
    // TODO: Can clean code up further
    private void showBookingDialog(final Offer offer) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_booking, null);
        builder.setView(dialogView);
    
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);

        TextView textViewDay = (TextView) dialogView.findViewById(R.id.text_view_day);
        TextView textViewMonth = (TextView) dialogView.findViewById(R.id.text_view_month);
        TextView textViewEndLocationName = (TextView) dialogView.findViewById(R.id.text_view_end_location_name);
        TextView textViewEndLocationAddress = (TextView) dialogView.findViewById(R.id.text_view_end_location_address);
        TextView textViewStartLocationName = (TextView) dialogView.findViewById(R.id.text_view_start_location_name);
        TextView textViewStartLocationAddress = (TextView) dialogView.findViewById(R.id.text_view_start_location_address);
        TextView textViewMeetupTime = (TextView) dialogView.findViewById(R.id.text_view_meetup_time);
        TextView textViewSeatsLeft = (TextView) dialogView.findViewById(R.id.text_view_seats_left);
        TextView textViewRemarksLabel = (TextView) dialogView.findViewById(R.id.text_view_remarks_label);
        TextView textViewRemarks = (TextView) dialogView.findViewById(R.id.text_view_remarks);
        ImageView imageViewDriverAvatar = (ImageView) dialogView.findViewById(R.id.image_view_driver_avatar);
        TextView textViewDriver = (TextView) dialogView.findViewById(R.id.text_view_driver);

        String meetUpTime = DateUtils.toFriendlyTimeString(offer.getMeetupTime());
        String day = DateUtils.dateToString(offer.getMeetupTime(), DateUtils.DAY_OF_MONTH_FORMAT);
        String month = DateUtils.dateToString(offer.getMeetupTime(), DateUtils.MONTH_ABBREVIATED_FORMAT);

        textViewMonth.setText(month);
        textViewDay.setText(day);
        textViewMeetupTime.setText(meetUpTime);
        textViewEndLocationName.setText(offer.getEndTerawhereLocation().getName());
        textViewEndLocationAddress.setText(offer.getEndTerawhereLocation().getAddress());
        textViewStartLocationName.setText(offer.getStartTerawhereLocation().getName());
        textViewStartLocationAddress.setText(offer.getStartTerawhereLocation().getAddress());
        textViewSeatsLeft.setText(Integer.toString(offer.getSeatsRemaining()));
        if (offer.getRemarks() != null && !offer.getRemarks().isEmpty()) {
            textViewRemarks.setText(offer.getRemarks());
        } else {
            textViewRemarksLabel.setVisibility(View.GONE);
            textViewRemarks.setVisibility(View.GONE);
        }
        Picasso.with(getContext())
                .load(offer.getOffererDp())
                .transform(new CropCircleTransformation())
                .into(imageViewDriverAvatar);
        textViewDriver.setText(offer.getOffererName());
    
        List<String> categories = new ArrayList<String>();
        int seatsAvailable = offer.getSeatsRemaining() < 2 ? offer.getSeatsRemaining() : 2;
        for (int i = 1; i <= seatsAvailable; i++) {
            categories.add(Integer.toString(i));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(dialogView.getContext(), android.R.layout.simple_spinner_item, categories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getView(position, convertView, parent));
            }
    
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getDropDownView(position, convertView, parent));
            }
    
            private View setCentered(View view) {
                view.setPadding(10, 20, 10, 10);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);
                return view;
            }
        };
    
        spinner.setAdapter(dataAdapter);

        if (AppPrefs.with(TerawhereApplication.ApplicationContext).getUserId().equals(offer.getOffererId())) {
            ((View) spinner.getParent()).setVisibility(View.GONE);
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            builder.setPositiveButton("Book", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    showConfirmBookDialog(offer, spinner.getSelectedItem().toString());
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        AlertDialog alert = builder.create();
        alert.show();
        decorateAlertDialog(alert);
    }

    private void decorateAlertDialog(AlertDialog alertDialog) {
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#54d8bd"));
    }
    
    private void showConfirmBookDialog(final Offer offer, final String numSeats) {
        if (numSeats.matches("")) {
            Toast.makeText(getContext(), "Please enter number of seats", Toast.LENGTH_SHORT).show();
        } else {
            int offerId = offer.getOfferId();
    
            createBookingApi(new BookingRequestBody(offerId)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
    
                    if (response.isSuccessful()) {
                        final Dialog successDialog = new Dialog(getActivity());
                        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        successDialog.setContentView(R.layout.dialog_booking_successful);
                        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        successDialog.setCanceledOnTouchOutside(false);
                        successDialog.setCancelable(false);
        
                        Button okButton = (Button) successDialog.findViewById(R.id.button_ok);
                        TextView dialogInfo = (TextView) successDialog.findViewById(R.id.text_view_info);
                        TextView dialogNotice = (TextView) successDialog.findViewById(R.id.text_view_notice);

                        dialogInfo.setText(Html.fromHtml("Driver: <b>" + offer.getOffererName() + "</b>"
                                + "<br/>Brand: <b>" + offer.getVehicle().getModel() + "</b>"
                                + "<br/>Color: <b>" + offer.getVehicle().getDescription() + "</b>"
                                + "<br/>Number: <b>" + offer.getVehicle().getPlateNumber() + "</b>"));
                        dialogNotice.setText(Html.fromHtml("<b>Driver will only wait for you at most 5 mins. Be punctual!</b>"));
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                successDialog.dismiss();
                                EventBus.getDefault().post(new BookingCreatedEvent());
                                viewPager.setCurrentItem(2);
                            }
                        });
                        successDialog.show();
                    } else {
                        try {
                            TerawhereBackendServer.ErrorDatum.ParseErrorAndToast(getContext(), response);
                        } catch (IOException e) {
                            onFailure(call, e);
                        }
                    }
                }
        
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    TerawhereBackendServer.ErrorDatum.ToastUnknownError(getContext(), t);
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookingCreatedEvent(BookingCreatedEvent event) {
        loadMarkers();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOfferCreatedEvent(OfferCreatedEvent event) {
        loadMarkers();
    }
}