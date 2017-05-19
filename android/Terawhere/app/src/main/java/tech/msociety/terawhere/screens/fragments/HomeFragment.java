package tech.msociety.terawhere.screens.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.maps.android.clustering.ClusterManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.AndroidSdkChecker;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.TerawhereApplication;
import tech.msociety.terawhere.TerawherePermissionChecker;
import tech.msociety.terawhere.adapters.CustomInfoViewAdapter;
import tech.msociety.terawhere.events.LogoutEvent;
import tech.msociety.terawhere.globals.AppPrefs;
import tech.msociety.terawhere.maps.ClusterMarkerLocation;
import tech.msociety.terawhere.maps.ClusterRenderer;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.factories.OfferFactory;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getbookings.BookingDatum;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.GetOffersResponse;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getuser.GetUserDetailsResponse;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.setlocation.LocationDatum;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.utils.DateUtils;

import static tech.msociety.terawhere.screens.activities.CreateOfferActivity.MESSAGE_RESPONSE;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    protected GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private ViewPager viewPager;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
    
        if (AndroidSdkChecker.isMarshmallow()) {
            TerawherePermissionChecker.checkPermission(getActivity());
        }
        
        LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        
        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        Location location;
        
        if (network_enabled) {
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        }
    
        Log.i("LATITUDES", ":" + latitude);
        Log.i("LONGITUDES", ":" + longitude);
        initializeSupportMapFragment();
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
    
        if (AndroidSdkChecker.isMarshmallow()) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                this.googleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            this.googleMap.setMyLocationEnabled(true);
        }
    
        initMarkers();
    }
    
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
    
    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10);
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
    }
    
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    
    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getContext(), "Terawhere needs location services to work optimally", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    
    private void initMarkers() {
        final String userId = AppPrefs.with(TerawhereApplication.ApplicationContext).getUserId();
        Log.i("USER_ID", ":" + userId);
        Call<GetOffersResponse> callGetOffers = TerawhereBackendServer.getApiInstance().getNearbyOffers(new LocationDatum(latitude, longitude));
        callGetOffers.enqueue(new Callback<GetOffersResponse>() {
            @Override
            public void onResponse(Call<GetOffersResponse> call, Response<GetOffersResponse> response) {
    
                if (response.isSuccessful()) {
                    final ClusterManager<ClusterMarkerLocation> clusterManager = new ClusterManager<ClusterMarkerLocation>(getContext(), googleMap);
                    googleMap.clear();
                    clusterManager.clearItems();
                    googleMap.setOnCameraIdleListener(clusterManager);
                    googleMap.getUiSettings().setMapToolbarEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
        
                    GetOffersResponse getOffersResponse = response.body();
                    Log.i("GET_OFFERS", ":" + getOffersResponse.toString());
                    List<Offer> offers = OfferFactory.createFromResponse(getOffersResponse);
                    
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(latitude, longitude), 16));
                    final HashMap<LatLng, Offer> mapLocationOffer = new HashMap<>();
                    for (int i = 0; i < offers.size(); i++) {
                        TerawhereLocation startLocation = offers.get(i).getStartTerawhereLocation();
                        LatLng startLatLng = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
    
                        clusterManager.addItem(new ClusterMarkerLocation(offers.get(i).getOfferId(), startLatLng));
                        mapLocationOffer.put(startLatLng, offers.get(i));
                    }
                    clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoViewAdapter(LayoutInflater.from(getContext()), mapLocationOffer));
                    clusterManager.setOnClusterItemInfoWindowClickListener(
                            new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarkerLocation>() {
                                @Override
                                public void onClusterItemInfoWindowClick(ClusterMarkerLocation clusterMarkerLocation) {
                                    final Offer currentOffer = mapLocationOffer.get(clusterMarkerLocation.getPosition());
                                    Log.i("USER_ID2", ":" + currentOffer.getOffererId());

                                    if (userId.equals(currentOffer.getOffererId())) {
                                        viewPager.setCurrentItem(1);
                                    } else {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        
                                        final LayoutInflater inflater = getActivity().getLayoutInflater();
                                        final View dialogView = inflater.inflate(R.layout.dialog_booking, null);
                                        builder.setView(dialogView);
                                        
                                        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);
                                        TextView dialogStartingLocation = (TextView) dialogView.findViewById(R.id.dialogTextViewStartingLocation);
    
                                        TextView dialogDestination = (TextView) dialogView.findViewById(R.id.dialogTextViewEndingLocation);
                                        TextView dialogRemarks = (TextView) dialogView.findViewById(R.id.dialogTextViewRemarks);
                                        TextView dialogTimestamp = (TextView) dialogView.findViewById(R.id.dialogTextViewMeetUpTime);
                                        TextView dialogSeatsAvailable = (TextView) dialogView.findViewById(R.id.dialogTextViewSeatsAvailable);
                                        TextView dialogMonth = (TextView) dialogView.findViewById(R.id.dialogTextViewMonth);
                                        TextView dialogDay = (TextView) dialogView.findViewById(R.id.dialogTextViewDay);
    
                                        if (currentOffer.getRemarks().matches("")) {
                                            dialogRemarks.setText("Remarks: NIL");
        
                                        } else {
                                            dialogRemarks.setText("Remarks: " + currentOffer.getRemarks());
                                        }
                                        dialogStartingLocation.setText("Meeting Point: " + currentOffer.getStartTerawhereLocation().getAddress());
                                        dialogDestination.setText("Destination: " + currentOffer.getEndTerawhereLocation().getAddress());
                                        String meetUpTime = DateUtils.toFriendlyDateTimeString(currentOffer.getMeetupTime());
                                        String day = DateUtils.toString(currentOffer.getMeetupTime(), DateUtils.DAY_OF_MONTH_FORMAT);
                                        String month = DateUtils.toString(currentOffer.getMeetupTime(), DateUtils.MONTH_ABBREVIATED_FORMAT);
                                        if (!meetUpTime.matches("")) {
                                            dialogTimestamp.setText("Pick Up Time: " + meetUpTime);
                                        }
                                        dialogDay.setText(day);
                                        dialogMonth.setText(month);
    
                                        dialogSeatsAvailable.setText("Seats Left: " + Integer.toString(currentOffer.getVacancy()));
                                        
                                        List<String> categories = new ArrayList<String>();
                                        int seatsAvailable = currentOffer.getVacancy();
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
    
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
    
                                                if (spinner.getSelectedItem().toString().matches("")) {
                                                    Toast.makeText(getContext(), "Please enter number of seats", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    AlertDialog.Builder adb2 = new AlertDialog.Builder(getContext());
                                                    
                                                    LayoutInflater inflater = getActivity().getLayoutInflater();
        
                                                    adb2.setTitle("Are you sure you want to book " + spinner.getSelectedItem().toString() + " seats?");
        
                                                    adb2.setIcon(android.R.drawable.ic_dialog_alert);
        
                                                    adb2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Call<GetUserDetailsResponse> callUser = TerawhereBackendServer.getApiInstance().getStatus();
                                                            callUser.enqueue(new Callback<GetUserDetailsResponse>() {
                                                                @Override
                                                                public void onResponse(Call<GetUserDetailsResponse> call, Response<GetUserDetailsResponse> response) {
                                                                    if (response.isSuccessful()) {
                                                                        Log.i("RESPONSE", response.body().toString());
                                                                        Log.i("user id", response.body().user.id);
                                                                        int offerId = currentOffer.getOfferId();
                                                                        String seats = spinner.getSelectedItem().toString();
                                                                        String userId = response.body().user.id;
                                                                        
                                                                        BookingDatum booking = new BookingDatum(Integer.toString(offerId), userId, seats);
                                                                        Call<BookingDatum> call2 = createBookingApi(booking);
                                                                        call2.enqueue(new Callback<BookingDatum>() {
                                                                                          @Override
                                                                                          public void onResponse(Call<BookingDatum> call, Response<BookingDatum> response) {
    
                                                                                              if (response.isSuccessful()) {
                                                                                                  Log.i(MESSAGE_RESPONSE, ": " + response.message());
        
                                                                                              } else {
                                                                                                  try {
                                                                                                      Log.i(MESSAGE_RESPONSE, ": " + response.errorBody().string());
                                                                                                  } catch (IOException e) {
                                                                                                      e.printStackTrace();
                                                                                                  }
                                                                                              }
                                                                                          }
    
                                                                            @Override
                                                                                          public void onFailure(Call<BookingDatum> call, Throwable t) {
                                                                                          }
                                                                                      }
                                                                        );
    
                                                                    } else {
                                                                        Log.i("RESPONSE", response.errorBody().toString());
                                                                    }
                                                                }
        
                                                                @Override
                                                                public void onFailure(Call<GetUserDetailsResponse> call, Throwable t) {
                                                                    System.out.println(Arrays.toString(t.getStackTrace()));
                                                                }
                                                            });
                                                            Toast.makeText(getContext(), spinner.getSelectedItem().toString() + " SEATS HAVE BEEN BOOKED!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    adb2.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    adb2.show();
                                                }
                                            }
                                        });
    
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                        nbutton.setTextColor(Color.BLACK);
                                        nbutton.setText("Cancel");
                                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                        pbutton.setTextColor(Color.parseColor("#54d8bd"));
                                        pbutton.setText("Confirm");
                                    }
                                }
                            });
                    clusterManager.setRenderer(new ClusterRenderer(getContext(), googleMap,
                            clusterManager));
                    googleMap.setOnInfoWindowClickListener(clusterManager);
                    googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
                    googleMap.setOnMarkerClickListener(clusterManager);
                } else {
                    try {
                        Log.i("ERROR_OFFER", ": " + response.errorBody().string());
                        Log.i("ERROR_OFFER2", ": " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    
            @Override
            public void onFailure(Call<GetOffersResponse> call, Throwable t) {
                System.out.println(Arrays.toString(t.getStackTrace()));
            }
        });
    }
    
    private Call<BookingDatum> createBookingApi(BookingDatum booking) {
        return TerawhereBackendServer.getApiInstance().createBooking(booking);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            Toast.makeText(getContext(), "Refreshing...", Toast.LENGTH_LONG).show();
            initMarkers();
        } else if (item.getItemId() == R.id.logout) {
            EventBus.getDefault().post(new LogoutEvent());
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getContext() != null) {
                initMarkers();
            }
        }
    }
}