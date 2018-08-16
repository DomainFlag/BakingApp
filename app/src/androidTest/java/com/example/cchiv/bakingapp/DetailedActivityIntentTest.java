package com.example.cchiv.bakingapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

public class DetailedActivityIntentTest {

    private String MY_PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    @Rule
    public IntentsTestRule<DetailedRecipeActivity> mainActivityIntentsTestRule = new IntentsTestRule<>(
            DetailedRecipeActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Before
    public void setMenuVisibilityVisible() {
        mainActivityIntentsTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = mainActivityIntentsTestRule.getActivity().getSupportFragmentManager();
                for(Fragment fragment : fragmentManager.getFragments()) {
                    if(fragment instanceof RecipeFragment) {
                        RecipeFragment recipeFragment = (RecipeFragment) fragment;

                        recipeFragment.onToggleMenu(null);
                    }
                }
            }
        });
    }

    @Test
    public void testRecipeFollowIntent() {
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v("DetailedActivityIntentTest", intent.getAction());
                unregisterReceiver(this);
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        mainActivityIntentsTestRule.getActivity().getBaseContext().registerReceiver(broadcastReceiver, intentFilter);

        onView(withId(R.id.recipe_follow))
                .perform(click());
    }

    private void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        mainActivityIntentsTestRule.getActivity().getBaseContext().unregisterReceiver(broadcastReceiver);
    }

    @Test
    public void testRecipeDetailedIntent() {
        onView(withId(R.id.recipe_see_more))
                .perform(click());

        intended(allOf(
                hasComponent(StepActivity.class.getName()),
                hasExtra("id", 1),
                toPackage(MY_PACKAGE_NAME)));
    }
}
