package tech.msociety.terawhere.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.activities.MainActivity;
import tech.msociety.terawhere.adapters.OffersAdapter;
import tech.msociety.terawhere.models.Offer;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private Context mContext;
    private SupportMapFragment mSupportMapFragment;
    protected GoogleMap mMap;
    private ClusterMarkerLocation clickedClusterItem;
    GoogleApiClient mGoogleApiClient;
    Location mLastKnownLocation;
    Marker mCurrentLocationMarker;
    LocationRequest mLocationRequest;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        // latest version SDK after Marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }

        // randomly place markers within the vicinity of current location
        initMarkers();

        // Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You are here!");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrentLocationMarker = mMap.addMarker(markerOptions);

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
            }
            else {
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
                }
                else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(mContext, "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    //randomised markers
    private void initMarkers() {
        final ClusterManager<ClusterMarkerLocation> clusterManager = new ClusterManager<ClusterMarkerLocation>( mContext, mMap );
        mMap.setOnCameraIdleListener((GoogleMap.OnCameraIdleListener) clusterManager);
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Offers");



        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject offer : objects) {
                            ParseGeoPoint pgp = offer.getParseGeoPoint("CurrentLocation");
                            String id = offer.getObjectId();
                            clusterManager.addItem( new ClusterMarkerLocation( id, new LatLng( pgp.getLatitude(), pgp.getLongitude()) ) );
                            mapLocationOffer.put(new LatLng( pgp.getLatitude(), pgp.getLongitude()), new Offer(offer.getObjectId(),offer.getString("Name"), offer.getString("Destination"), offer.getInt("SeatsAvailable"), offer.getDate("PickUpTime"), offer.getString("Remarks")));
                        }


                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        clusterManager.getMarkerCollection()
                .setOnInfoWindowAdapter(new CustomInfoViewAdapter(LayoutInflater.from(mContext),mapLocationOffer));

        clusterManager.setOnClusterItemInfoWindowClickListener(
                new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarkerLocation>() {
                    @Override public void onClusterItemInfoWindowClick(ClusterMarkerLocation stringClusterItem) {
                        Toast.makeText(mContext, "Clicked info window: " + stringClusterItem.getId(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        mMap.setOnInfoWindowClickListener(clusterManager);
        mMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        //mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(clusterManager);





    }





}

