package tech.msociety.terawhere.screens.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.github.paolorotolo.appintro.AppIntroFragment;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.globals.AppPrefs;
import tech.msociety.terawhere.globals.TerawhereApplication;

public class OnboardingActivity extends AppIntro {
    public static void start(Context context) {
        Intent intent = new Intent(context, OnboardingActivity.class);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addSlide(AppIntro2Fragment.newInstance("Terawhere Is Free", "Offer rides as amal jariyah. \nNo fees. Just pahala.", R.drawable.terawhere_landing_page_logo_small, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntro2Fragment.newInstance("Rides Around You", "The map shows rides around you. \nZoom out to see more rides.", R.drawable.onboarding_find_rides_nearby, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Finding Rides", "Available rides show as pins on the map. \nClick on a pin to find out more.", R.drawable.onboarding_find_rides_on_map, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Offering Rides", "To offer rides, click on the round plus button on the bottom right.", R.drawable.onboarding_offer_ride, ContextCompat.getColor(this, R.color.colorPrimary)));
        
        setBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSeparatorColor(ContextCompat.getColor(this, R.color.white));
        
        showSkipButton(true);
        setProgressButtonEnabled(true);
    }
    
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        onDonePressed(currentFragment);
    }
    
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        
        AppPrefs.with(TerawhereApplication.ApplicationContext).setOnboardingToBeShown(false);
        finish();   // this assumes MainActivity is right below this on the activity stack
    }
}
