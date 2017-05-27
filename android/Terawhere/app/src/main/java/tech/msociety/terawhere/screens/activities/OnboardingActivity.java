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
        
        addSlide(AppIntro2Fragment.newInstance("Terawhere Is Free!", "Offer free rides to Terawih\nEase their travel.", R.drawable.terawhere_landing_page_logo_small, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntro2Fragment.newInstance("Find Rides Around You", "The map shows you the ride offers around you.\nZoom out to view more.", R.drawable.onboarding_find_rides_nearby, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("View Ride Info", "The pins on the map indicates the rides.\nTap on it for ride details.", R.drawable.onboarding_ride_details, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Book The Ride", "Indicate the number of seats you need and book your free ride!", R.drawable.onboarding_book, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Offering Rides", "Under the \"Offer a Ride\" tab, tap on the + button on the bottom right.", R.drawable.onboarding_offer_ride, ContextCompat.getColor(this, R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Wait for Bookings", "We’ll notify you of any bookings made by others.", R.drawable.onboarding_offer_created, ContextCompat.getColor(this, R.color.colorPrimary)));
        
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
