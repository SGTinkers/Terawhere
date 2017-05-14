package tech.msociety.terawhere.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tech.msociety.terawhere.screens.fragments.HomeFragment;
import tech.msociety.terawhere.screens.fragments.MyBookingsFragment;
import tech.msociety.terawhere.screens.fragments.MyOffersFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    private int numTabs;

    public PagerAdapter(FragmentManager fragmentManager, int numTabs) {
        super(fragmentManager);
        this.numTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new MyOffersFragment();
            case 2:
                return new MyBookingsFragment();
            default:
                return null;
        }

    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}