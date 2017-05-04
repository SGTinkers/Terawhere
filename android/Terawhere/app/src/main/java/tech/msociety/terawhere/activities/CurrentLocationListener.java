package tech.msociety.terawhere.activities;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by musa on 4/5/17.
 */

     class CurrentLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("LOCATION CHANGED", location.getLatitude() + "");
                Log.d("LOCATION CHANGED", location.getLongitude() + "");

            }
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

