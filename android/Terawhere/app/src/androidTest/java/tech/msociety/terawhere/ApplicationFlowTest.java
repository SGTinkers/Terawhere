package tech.msociety.terawhere;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tech.msociety.terawhere.screens.activities.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ApplicationFlowTest {
    private static final String TAB_LABEL_HOME = "Home";
    private static final String TAB_LABEL_MY_OFFERS = "My Offers";
    private static final String TAB_LABEL_MY_BOOKINGS = "My Bookings";
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);
    
    @Test
    public void canLoadMainActivityAndMoveBetweenTabs() {
        checkToolbarTitleIs(TAB_LABEL_HOME);
        
        onView(withText(TAB_LABEL_MY_OFFERS)).perform(click());
        // TODO: assert that the data loads in list
        checkToolbarTitleIs(TAB_LABEL_MY_OFFERS);
        
        onView(withText(TAB_LABEL_HOME)).perform(click());
        // TODO: assert that the data loads in list
        checkToolbarTitleIs(TAB_LABEL_HOME);

//        onView(withId(R.id.floating_action_button_add_record)).perform(click());
//        checkToolbarTitleIs("Create New Job");
//        navigateUp();
//        // TODO: this might crash if network call is slow, cos the progress dialog is still showing
//
//        checkToolbarTitleIs("IMAN");
//        onView(withId(R.id.action_approve_job_applicants)).perform(click());
//        checkToolbarTitleIs("Jobs At My Location");
//        navigateUp();
//        // TODO: this might crash if network call is slow, cos the progress dialog is still showing
//
//        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
//        onView(withText("Settings")).perform(click());
//        checkToolbarTitleIs("Settings");
    }
    
    private void navigateUp() {
        onView(withContentDescription("Navigate up")).perform(click());
    }
    
    private void checkToolbarTitleIs(String toolbarTitle) {
        onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(toolbarTitle)));
    }
}
