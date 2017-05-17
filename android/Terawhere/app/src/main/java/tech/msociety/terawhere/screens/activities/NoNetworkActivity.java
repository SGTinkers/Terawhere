package tech.msociety.terawhere.screens.activities;

import android.os.Bundle;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.screens.activities.abstracts.BaseActivity;

public class NoNetworkActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network);
        
        requireAuth = false;
        requireNetwork = false;
    }
}
