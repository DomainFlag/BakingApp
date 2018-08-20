package com.example.cchiv.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.fragments.MasterListFragment;
import com.example.cchiv.bakingapp.fragments.RecipeFragment;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.util.BakingLoader;

public class RecipeActivity extends AppCompatActivity implements BakingLoader.OnInterfaceRecipeCallback {

    private static final String RECIPE_FRAGMENT_KEY = "RECIPE_FRAGMENT_KEY";

    private RecipeFragment recipeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_recipe);

        BakingLoader bakingLoader;

        if(savedInstanceState != null) {
            bakingLoader = new BakingLoader(this, this, savedInstanceState.getInt("id"));

            recipeFragment = (RecipeFragment) getSupportFragmentManager().getFragment(savedInstanceState, RECIPE_FRAGMENT_KEY);
        } else {
            Intent intent = getIntent();
            int id = intent.getIntExtra("id", -1);

            bakingLoader = new BakingLoader(this, this, id);
        }

        getSupportLoaderManager().initLoader(MasterListFragment.LOADER_RECIPES, null, bakingLoader).forceLoad();
    }

    @Override
    public void OnInterfaceRecipeUpdateCallback(Recipe recipe) {
        if(recipe == null) finish();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(recipeFragment == null) {
            recipeFragment = new RecipeFragment();
            recipeFragment.onRecipeAttach(recipe);

            fragmentTransaction
                    .add(R.id.recipe_detailed, recipeFragment);
        } else {
            recipeFragment.onChangeRecipe(recipe);

            fragmentTransaction
                    .replace(R.id.recipe_detailed, recipeFragment);
        }

        fragmentTransaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if(id != -1) {
            outState.putInt("id", id);
        }

        if(recipeFragment != null)
            getSupportFragmentManager().putFragment(outState, RECIPE_FRAGMENT_KEY, recipeFragment);
    }
}
