package tech.msociety.terawhere.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import tech.msociety.terawhere.R;

public class CreateOfferActivity extends BaseActivity {
    private static final String TOOLBAR_TITLE = "Create Offer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);

        initToolbar(TOOLBAR_TITLE, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
