package com.example.cchiv.bakingapp;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.cchiv.bakingapp.obj.Step;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StepActivityButtonsTest {

    private String MY_PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    @Rule
    public ActivityTestRule<StepActivity> mStepActivityRule =
            new ActivityTestRule<>(StepActivity.class);

    @Before
    public void setUpIntentActivity() {
        Intent intent = new Intent();
        intent.putExtra("id", -1);
        mStepActivityRule.launchActivity(intent);
    }

    @Test
    public void testLeftArrowButton() {
        onView(withId(R.id.previous_step)).check(matches(not(isDisplayed())));
        onView(withId(R.id.next_step)).check(matches(isDisplayed()));
    }

    @Test
    public void testRightArrowButton() {
        new Thread() {
            @Override
            public void run() {
                mStepActivityRule.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Step> steps = mStepActivityRule.getActivity().getSteps();
                        if(steps != null && steps.size() != 0) {
                            mStepActivityRule.getActivity().renderStepContent(null, steps.size() - 1);

                            onView(withId(R.id.previous_step)).check(matches(isDisplayed()));
                            onView(withId(R.id.next_step)).check(matches(not(isDisplayed())));
                        }
                    }
                });
            }
        };
    }

//    @Rule
//    public IntentsTestRule<DetailedRecipeActivity> mainActivityIntentsTestRule = new IntentsTestRule<>(
//            DetailedRecipeActivity.class);
//
//    @Before
//    public void stubAllExternalIntents() {
//        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));
//    }
//
//    @Test
//    public void testRecipeFollowIntent() {
//        onView(withId(R.id.recipe_follow))
//                .perform(click());
//
//        intended(allOf(
//                hasAction(Intent.ACTION_CALL),
//                hasExtra("type", BakingWidgetProvider.UPDATE_RECIPE),
//                hasExtra("id", greaterThan(0)),
//                toPackage(MY_PACKAGE_NAME)));
//    }
}
