package com.example.cchiv.bakingapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.cchiv.bakingapp.activities.RecipeActivity;
import com.example.cchiv.bakingapp.fragments.RecipeFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DetailedActivityBroadcastTest {

    private BroadcastReceiver broadcastReceiver;

    private BakingIdlingResource bakingIdlingResource = new BakingIdlingResource();

    @Rule
    public IntentsTestRule<RecipeActivity> mainActivityIntentsTestRule = new IntentsTestRule<>(
            RecipeActivity.class);

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

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
                    bakingIdlingResource.changeIdleState(false);
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        mainActivityIntentsTestRule.getActivity().getBaseContext().registerReceiver(broadcastReceiver, intentFilter);

        onView(withId(R.id.recipe_follow))
                .perform(click());
    }

    @Test
    public void testRecipeFollowIntent() {
        bakingIdlingResource.changeIdleState(true);
        IdlingRegistry.getInstance().register(bakingIdlingResource);
    }

    @After
    public void unregisterBroadcastReceiver() {
        mainActivityIntentsTestRule.getActivity().getBaseContext().unregisterReceiver(broadcastReceiver);
    }
}
