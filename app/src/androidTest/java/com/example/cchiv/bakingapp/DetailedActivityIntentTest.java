package com.example.cchiv.bakingapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.cchiv.bakingapp.activities.RecipeActivity;
import com.example.cchiv.bakingapp.activities.StepActivity;
import com.example.cchiv.bakingapp.fragments.RecipeFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DetailedActivityIntentTest {

    private final static String MY_PACKAGE_NAME = BuildConfig.APPLICATION_ID;

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

                        recipeFragment.onToggleMenu();
                    }
                }
            }
        });
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
