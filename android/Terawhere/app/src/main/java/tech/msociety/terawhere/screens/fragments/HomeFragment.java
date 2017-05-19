package tech.msociety.terawhere.screens.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import android.widget.AdapterView;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.adapters.CustomInfoViewAdapter;
import tech.msociety.terawhere.events.LogoutEvent;
import tech.msociety.terawhere.maps.ClusterMarkerLocation;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getbookings.BookingDatum;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.GetOffersResponse;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getuser.GetUser;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.setlocation.LocationDatum;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;

import static tech.msociety.terawhere.screens.activities.CreateOfferActivity.MESSAGE_RESPONSE;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private Context context;
    private SupportMapFragment supportMapFragment;
    protected GoogleMap googleMap;
    private LocationManager locationManager;

    GoogleApiClient googleApiClient;
    Location currentLocation;
    LocationRequest locationRequest;

    double latitude = 0.0;
    double longitude = 0.0;

    ViewPager viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initializeContext();
        setHasOptionsMenu(true);


        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeContext();
        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);


        if (isMinimumSdkMarshmallow()) {
            checkLocationPermission();
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

    private boolean isMinimumSdkMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void initializeSupportMapFragment() {
        supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_container);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
    }

    private void initializeContext() {
        context = getActivity();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (isMinimumSdkMarshmallow()) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                this.googleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            this.googleMap.setMyLocationEnabled(true);
        }

        getUserId();

    }

    // Create connection with google maps
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10);
        locationRequest.setFastestInterval(10);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
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
        currentLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveCameraToLocation(latLng);
        zoomCameraToLocation();
        stopLocationUpdates();
    }

    private void zoomCameraToLocation() {
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    private void moveCameraToLocation(LatLng latLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void getUserId() {
        TerawhereBackendServer.getApiInstance().getStatus().enqueue(new Callback<GetUser>() {
            @Override
            public void onResponse(Call<GetUser> call, Response<GetUser> response) {

                if (response.isSuccessful()) {
                    Log.i("RESPONSE", response.body().toString());
                    Log.i("user id", response.body().getUser().getId());
                    initMarkers(response.body().getUser().getId());

                } else {
                    Log.i("RESPONSE", response.errorBody().toString());

                   /* try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.i("ERROR", ":" + jObjError.getString("error"));
                        if (jObjError.getString("error").equals("token_expired")) {
                            //refresh token
                        }

                    } catch (Exception e) {
                    }*/
                }


            }

            @Override
            public void onFailure(Call<GetUser> call, Throwable t) {
                Log.i("FAILURE", Arrays.toString(t.getStackTrace()));

                System.out.println(Arrays.toString(t.getStackTrace()));

            }
        });
    }

    private Call<BookingDatum> createBookingApi(BookingDatum booking) {
        return TerawhereBackendServer.getApiInstance().createBooking(booking);
    }

    private void initMarkers(final String userId) {


        Log.i("LATITUDE", Double.toString(latitude));
        Log.i("LONGITUDE", Double.toString(longitude));
        LocationDatum locationDatum = new LocationDatum(latitude, longitude);
        Call<GetOffersResponse> callGetOffers = TerawhereBackendServer.getApiInstance().getNearbyOffers(new LocationDatum(latitude, longitude));
        callGetOffers.enqueue(new Callback<GetOffersResponse>() {
            @Override
            public void onResponse(Call<GetOffersResponse> call, Response<GetOffersResponse> response) {

                if (response.isSuccessful()) {
                    final ClusterManager<ClusterMarkerLocation> clusterManager = new ClusterManager<ClusterMarkerLocation>(context, googleMap);
                    googleMap.clear();
                    clusterManager.clearItems();
                    googleMap.setOnCameraIdleListener(clusterManager);
                    googleMap.getUiSettings().setMapToolbarEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
    
                    GetOffersResponse getOffersResponse = response.body();
                    Log.i("GET_OFFERS", ":" + getOffersResponse.toString());
//                    final List<Offer> offers = getOffersResponse.getOffers();
                    final List<Offer> offers = new ArrayList<>();

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(latitude, longitude), 16));
                    final HashMap<LatLng, Offer> mapLocationOffer = new HashMap<>();
                    for (int i = 0; i < offers.size(); i++) {
                        clusterManager.addItem(new ClusterMarkerLocation(offers.get(i).getId(), new LatLng(offers.get(i).getStartingLocationLatitude(), offers.get(i).getStartingLocationLongitude())));
                        mapLocationOffer.put(new LatLng(offers.get(i).getStartingLocationLatitude(), offers.get(i).getStartingLocationLongitude()), offers.get(i));
                    }
                    Log.i("SIZE OF MAP", ":" + mapLocationOffer.size());


                    clusterManager.getMarkerCollection()
                            .setOnInfoWindowAdapter(new CustomInfoViewAdapter(LayoutInflater.from(context), mapLocationOffer));

                    clusterManager.setOnClusterItemInfoWindowClickListener(
                            new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarkerLocation>() {


                                @Override
                                public void onClusterItemInfoWindowClick(ClusterMarkerLocation clusterMarkerLocation) {
                                    final Offer currentOffer = mapLocationOffer.get(clusterMarkerLocation.getPosition());
                                    Log.i("OFFERDETAILS", ":" + currentOffer.toString());
                                    //if (currentOffer.getDriverId().equals())
                                    if (userId.equals(currentOffer.getDriverId())) {
                                        viewPager.setCurrentItem(1);
                                    } else {
                                        final AlertDialog.Builder adb = new AlertDialog.Builder(context);

                                        final LayoutInflater inflater = getActivity().getLayoutInflater();
                                        final View dialogView = inflater.inflate(R.layout.dialog_booking, null);
                                        adb.setView(dialogView);


                                        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);
                                        TextView dialogStartingLocation = (TextView) dialogView.findViewById(R.id.dialogTextViewStartingLocation);

                                        TextView dialogDestination = (TextView) dialogView.findViewById(R.id.dialogTextViewEndingLocation);
                                        TextView dialogRemarks = (TextView) dialogView.findViewById(R.id.dialogTextViewRemarks);
                                        TextView dialogTimestamp = (TextView) dialogView.findViewById(R.id.dialogTextViewMeetUpTime);
                                        TextView dialogSeatsAvailable = (TextView) dialogView.findViewById(R.id.dialogTextViewSeatsAvailable);
                                        TextView dialogMonth = (TextView) dialogView.findViewById(R.id.dialogTextViewMonth);
                                        TextView dialogDay = (TextView) dialogView.findViewById(R.id.dialogTextViewDay);


                                        if (currentOffer.getDriverRemarks().matches("")) {
                                            dialogRemarks.setText("Remarks: NIL");

                                        } else {
                                            dialogRemarks.setText("Remarks: " + currentOffer.getDriverRemarks());
                                        }
                                        dialogDestination.setText("Destination: " + currentOffer.getEndingLocationAddress());
                                        dialogStartingLocation.setText("Meeting Point: " + currentOffer.getStartingLocationAddress());
                                        String meetUpTime = "";
                                        String day = "";
                                        String month = "";
                                        try {
                                            meetUpTime = new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").parse(currentOffer.getMeetUpTime().toString()));
                                            day = new SimpleDateFormat("dd").format(new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").parse(currentOffer.getMeetUpTime().toString()));
                                            month = new SimpleDateFormat("MMMM").format(new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").parse(currentOffer.getMeetUpTime().toString()));


                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if (!meetUpTime.matches("")) {
                                            dialogTimestamp.setText("Pick Up Time: " + meetUpTime);
                                        }
                                        dialogDay.setText(day);
                                        dialogMonth.setText(month);

                                        dialogSeatsAvailable.setText("Seats Left: " + Integer.toString(currentOffer.getSeatsAvailable()));


                                        List<String> categories = new ArrayList<String>();
                                        int seatsAvailable = currentOffer.getSeatsAvailable();
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
                                        spinner.setOnItemSelectedListener(new OnSpinnerItemClicked());

                                        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {


                                                if (spinner.getSelectedItem().toString().matches("")) {
                                                    Toast.makeText(context, "Please enter number of seats", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    AlertDialog.Builder adb2 = new AlertDialog.Builder(context);

                                                    LayoutInflater inflater = getActivity().getLayoutInflater();


                                                    adb2.setTitle("Are you sure you want to book " + spinner.getSelectedItem().toString() + " seats?");


                                                    adb2.setIcon(android.R.drawable.ic_dialog_alert);


                                                    adb2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            Call<GetUser> callUser = TerawhereBackendServer.getApiInstance().getStatus();

                                                            callUser.enqueue(new Callback<GetUser>() {
                                                                @Override
                                                                public void onResponse(Call<GetUser> call, Response<GetUser> response) {

                                                                    if (response.isSuccessful()) {
                                                                        Log.i("RESPONSE", response.body().toString());
                                                                        Log.i("user id", response.body().getUser().getId());
                                                                        int offerId = currentOffer.getId();
                                                                        String seats = spinner.getSelectedItem().toString();
                                                                        String userId = response.body().getUser().getId();

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
                                                                public void onFailure(Call<GetUser> call, Throwable t) {
                                                                    Log.i("FAILURE", Arrays.toString(t.getStackTrace()));

                                                                    System.out.println(Arrays.toString(t.getStackTrace()));

                                                                }
                                                            });


                                                            Toast.makeText(context, spinner.getSelectedItem().toString() + " SEATS HAVE BEEN BOOKED!", Toast.LENGTH_SHORT).show();
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


                                        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        //adb.show();
                                        AlertDialog alert = adb.create();
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
                    clusterManager.setRenderer(new MyClustererRenderer(context, googleMap,
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


    public class OnSpinnerItemClicked implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            Toast.makeText(parent.getContext(), "Clicked : " +
                    parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();


        }

        @Override
        public void onNothingSelected(AdapterView parent) {
// Do nothing.
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.refresh) {
            Toast.makeText(context, "Refreshing...", Toast.LENGTH_LONG).show();
            getUserId();

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
                getUserId();
            }
        }
    }

    public class MyClustererRenderer extends DefaultClusterRenderer<ClusterMarkerLocation> {

        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity().getApplicationContext());

        public MyClustererRenderer(Context context, GoogleMap map,
                                   ClusterManager<ClusterMarkerLocation> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterMarkerLocation item,
                                                   MarkerOptions markerOptions) {

            BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.pin_location);

            markerOptions.icon(markerDescriptor);
        }

        @Override
        protected void onClusterItemRendered(ClusterMarkerLocation clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<ClusterMarkerLocation> cluster, MarkerOptions markerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions);

        }
    }

}

