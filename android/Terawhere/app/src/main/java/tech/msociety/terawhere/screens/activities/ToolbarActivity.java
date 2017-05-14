package tech.msociety.terawhere.screens.activities;

import android.support.v7.widget.Toolbar;

import tech.msociety.terawhere.R;

public abstract class ToolbarActivity extends BaseActivity {
    protected Toolbar toolbar;
    
    protected void initToolbar(String toolbarTitle, boolean displayHomeAsUpEnabled) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled);
    }
}
