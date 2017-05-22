package tech.msociety.terawhere.screens.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.events.LogoutEvent;
import tech.msociety.terawhere.screens.activities.abstracts.BaseActivity;
import tech.msociety.terawhere.screens.fragments.HomeFragment;
import tech.msociety.terawhere.screens.fragments.MyBookingsFragment;
import tech.msociety.terawhere.screens.fragments.MyOffersFragment;

public class MainActivity extends BaseActivity {
    private Toolbar toolbar;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private Context context = this;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeToolbar();
        initializeTabLayout();
        initPagerView();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            EventBus.getDefault().post(new LogoutEvent());
        }

        if(item.getItemId() == R.id.action_info){

            Uri uri = Uri.parse("https://terawhere.com/#features"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    
    private void initializeTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.mainActivityTabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_drives));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_pickups));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_rides));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }
    
    private void initPagerView() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(1);
    }

    /**
     * This is here so that it'll be easy for us to manage/change tabs.
     * The other reason is that this PagerAdapter is tightly coupled to this activity.
     */
    private class PagerAdapter extends FragmentPagerAdapter {

        private int numTabs;

        private Fragment[] fragments;

        public PagerAdapter(FragmentManager fragmentManager, int numTabs) {
            super(fragmentManager);
            this.numTabs = numTabs;
            this.fragments = new Fragment[numTabs];
        }

        @Override
        public Fragment getItem(int position) {
            if (fragments[position] != null) {
                return fragments[position];
            }

            switch (position) {
                case 0:
                    fragments[0] = new MyOffersFragment();
                    break;
                case 1:
                    fragments[1] =  new HomeFragment();
                    break;
                case 2:
                    fragments[2] =  new MyBookingsFragment();
                    break;
            }

            return fragments[position];
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof MyOffersFragment) {
                return 0;
            } else if (object instanceof HomeFragment) {
                return 1;
            } else if (object instanceof MyBookingsFragment) {
                return 2;
            }

            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return numTabs;
        }

    }
}
