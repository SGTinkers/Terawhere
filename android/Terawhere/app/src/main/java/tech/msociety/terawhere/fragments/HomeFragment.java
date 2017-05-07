package tech.msociety.terawhere.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.activities.LoginActivity;
import tech.msociety.terawhere.adapters.CustomInfoViewAdapter;
import tech.msociety.terawhere.maps.ClusterMarkerLocation;
import tech.msociety.terawhere.models.Offer;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context mContext;
    private SupportMapFragment mSupportMapFragment;
    protected GoogleMap mMap;
    private ClusterMarkerLocation clickedClusterItem;
    GoogleApiClient mGoogleApiClient;
    Location mLastKnownLocation;
    //Marker mCurrentLocationMarker;
    LocationRequest mLocationRequest;
    ClusterManager<ClusterMarkerLocation> clusterManager;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();


// latest version SDK after Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

// create map in fragment
        FragmentManager fm = getActivity().getSupportFragmentManager();/// getChildFragmentManager();
        mSupportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mSupportMapFragment).commit();
        }
        mSupportMapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        initMarkers();

// latest version SDK after Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    // Create connection with google maps
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastKnownLocation = location;
//if (mCurrentLocationMarker != null) {
//     mCurrentLocationMarker.remove();
// }

// randomly place markers within the vicinity of current location

// Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You are here!");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
// mCurrentLocationMarker = mMap.addMarker(markerOptions);

// move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

// stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

// Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
// Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
// No explanation needed, we can request the permission.
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
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// permission was granted

                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
// Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(mContext, "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            Log.i("HELLO THERE", " HEHE");


        }
    }

    //randomised markers
    private void initMarkers() {
        final ClusterManager<ClusterMarkerLocation> clusterManager = new ClusterManager<ClusterMarkerLocation>(mContext, mMap);
        mMap.clear();
        clusterManager.clearItems(); // calling for sure - maybe it doenst need to be here
        mMap.setOnCameraIdleListener(clusterManager);
//mMap.setOnMarkerClickListener(clusterManager);
//mMap.setOnInfoWindowClickListener(clusterManager);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
//clusterManager.setOnClusterClickListener(this);
//clusterManager.setOnClusterInfoWindowClickListener(this);
//clusterManager.setOnClusterItemClickListener(this);
//clusterManager.setOnClusterItemInfoWindowClickListener(this);

//clusterManager.setOnClusterItemInfoWindowClickListener(this); //added

        final HashMap<LatLng, Offer> mapLocationOffer = new HashMap<>();

        clusterManager.getMarkerCollection()
                .setOnInfoWindowAdapter(new CustomInfoViewAdapter(LayoutInflater.from(mContext), mapLocationOffer));

        clusterManager.setOnClusterItemInfoWindowClickListener(
                new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarkerLocation>() {
                    @Override
                    public void onClusterItemInfoWindowClick(ClusterMarkerLocation stringClusterItem) {
                        Offer currentOffer = mapLocationOffer.get(stringClusterItem.getPosition());

                        final AlertDialog.Builder adb = new AlertDialog.Builder(mContext);

                        final LayoutInflater inflater = getActivity().getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.dialog_booking, null);
                        adb.setView(dialogView);

                        adb.setTitle(currentOffer.getDriverId());


                        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);
                        TextView dialogDestination = (TextView) dialogView.findViewById(R.id.dialogDestination);
                        TextView dialogRemarks = (TextView) dialogView.findViewById(R.id.dialogRemarks);
                        TextView dialogTimestamp = (TextView) dialogView.findViewById(R.id.dialogTimestamp);
                        TextView dialogSeatsAvailable = (TextView) dialogView.findViewById(R.id.dialogSeatsAvailable);

                        dialogRemarks.setText(currentOffer.getRemarks());
                        dialogDestination.setText(currentOffer.getDestination());
                        SimpleDateFormat ft = new SimpleDateFormat("hh:mm a");

                        if (currentOffer.getTimestamp() != null) {
                            dialogTimestamp.setText(ft.format(currentOffer.getTimestamp()));
                        }
                        dialogSeatsAvailable.setText(Integer.toString(currentOffer.getNumberOfSeats()) + " LEFT");


// Spinner Drop down elements
                        List<String> categories = new ArrayList<String>();
                        stringClusterItem.getPosition();
                        int seatsAvailable = currentOffer.getNumberOfSeats();
                        for (int i = 1; i <= seatsAvailable; i++) {
                            categories.add(Integer.toString(i));

                        }


// Creating adapter for spinner
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
// Drop down layout style - list view with radio button

// attaching data adapter to spinner
                        spinner.setAdapter(dataAdapter);
                        spinner.setOnItemSelectedListener(new OnSpinnerItemClicked());

                        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


// CONFIRMATION BUTTON
                                if (spinner.getSelectedItem().toString().matches("")) {
                                    Toast.makeText(mContext, "Please enter number of seats", Toast.LENGTH_SHORT).show();
                                } else {
                                    AlertDialog.Builder adb2 = new AlertDialog.Builder(mContext);

                                    LayoutInflater inflater = getActivity().getLayoutInflater();


                                    adb2.setTitle("Are you sure you want to book " + spinner.getSelectedItem().toString() + " seats?");


                                    adb2.setIcon(android.R.drawable.ic_dialog_alert);


                                    adb2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {


                                            Toast.makeText(mContext, spinner.getSelectedItem().toString() + " SEATS HAVE BEEN BOOKED!", Toast.LENGTH_SHORT).show();
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
                        adb.show();
                    }
                });

        mMap.setOnInfoWindowClickListener(clusterManager);
        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
//mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(clusterManager);


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

            Toast.makeText(mContext, "Refreshing...", Toast.LENGTH_LONG).show();
//mMap.clear();

            initMarkers();

        } else if (item.getItemId() == R.id.logout) {


            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
}

