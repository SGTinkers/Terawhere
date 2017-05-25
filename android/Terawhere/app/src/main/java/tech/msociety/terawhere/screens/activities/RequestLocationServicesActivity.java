package tech.msociety.terawhere.screens.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.screens.activities.abstracts.BaseActivity;

public class RequestLocationServicesActivity extends BaseActivity {

    private Button buttonTurnOnLocationServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requireAuth = false;
        requireLocationServices = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_location_services);

        buttonTurnOnLocationServices = (Button) findViewById(R.id.buttonTurnOnLocationServices);

        buttonTurnOnLocationServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTurnOnLocationServices.setEnabled(false);
                ActivityCompat.requestPermissions(RequestLocationServicesActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, R.string.error_need_location_services, Toast.LENGTH_SHORT).show();
                }
                buttonTurnOnLocationServices.setEnabled(true);
                return;
            }
        }
    }
}
