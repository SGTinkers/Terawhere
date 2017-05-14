package tech.msociety.terawhere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.adapters.PagerAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TOOLBAR_TITLE = "Home";
    public static final String MESSAGE_HELLO_TERAWHERE = "Hello Terawhere!";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int viewPagerPosition = -1;

    private static long TIME_MINUTES = 60 * 10 * 1000;
    private static final long INTERVAL = 1000;
    MyCountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeCountDown();

        initializeToolBar();
        initializeTabLayout();
        initializePagerView();

        Toast.makeText(this, MESSAGE_HELLO_TERAWHERE, Toast.LENGTH_SHORT).show();
    }

    private void initializeCountDown() {
        countDownTimer = new MyCountDownTimer(TIME_MINUTES, INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        countDownTimer.cancel();
        countDownTimer.start();
    }

    //enable user countdown when app is in idle mode
    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }


    /*
    *************** Initialise methods ************************
    */

    private void initializeToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TOOLBAR_TITLE);
    }

    private void initializeTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.mainActivityTabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("My Offers"));
        tabLayout.addTab(tabLayout.newTab().setText("My Bookings"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void initializePagerView() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        toolbar.setTitle(TOOLBAR_TITLE);
                        break;
                    case 1:
                        toolbar.setTitle("My Offers");
                        break;
                    case 2:
                        toolbar.setTitle("My Bookings");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Intent i = getIntent();
        int tabToOpen = i.getIntExtra("FirstTab", -1);
        viewPager.setCurrentItem(1);

        if (tabToOpen != -1) {
            viewPagerPosition = -1;
            viewPager.setCurrentItem(1);
        } else if (viewPagerPosition != -1) {
            viewPager.setCurrentItem(viewPagerPosition);
            viewPagerPosition = -1;
        }
    }

}
