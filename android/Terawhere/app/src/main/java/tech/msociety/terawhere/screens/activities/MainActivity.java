package tech.msociety.terawhere.screens.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.adapters.PagerAdapter;
import tech.msociety.terawhere.screens.activities.abstracts.BaseActivity;

public class MainActivity extends BaseActivity {
    private static final String TOOLBAR_TITLE = "Home";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int viewPagerPosition = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeToolBar();
        initializeTabLayout();
        initPagerView();
    }
    
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
    
    private void initPagerView() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        
        if (viewPagerPosition != -1) {
            viewPager.setCurrentItem(viewPagerPosition);
            viewPagerPosition = -1;
        }
    }
}
