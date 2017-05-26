package tech.msociety.terawhere.screens.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.screens.activities.abstracts.BaseActivity;

public class RequestLocationServicesActivity extends BaseActivity {

    private Button buttonTurnOnLocationServices;

    private  LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requireAuth = false;
        requireLocationServices = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_location_services);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        buttonTurnOnLocationServices = (Button) findViewById(R.id.buttonTurnOnLocationServices);

        buttonTurnOnLocationServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(RequestLocationServicesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    buttonTurnOnLocationServices.setEnabled(false);
                    ActivityCompat.requestPermissions(RequestLocationServicesActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            0);
                } else {
                    checkIfLocationServiceEnabled();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateButtonText();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    checkIfLocationServiceEnabled();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, R.string.error_need_location_services_permission, Toast.LENGTH_SHORT).show();
                }
                buttonTurnOnLocationServices.setEnabled(true);
                updateButtonText();
                return;
            }
        }
    }

    private void checkIfLocationServiceEnabled() {
        boolean locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (locationEnabled) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, R.string.error_need_location_services, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateButtonText() {
        int permissionCheck = ContextCompat.checkSelfPermission(RequestLocationServicesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            buttonTurnOnLocationServices.setText("GRANT LOCATION PERMISSION");
        } else {
            buttonTurnOnLocationServices.setText("PROCEED");
        }
    }
}
