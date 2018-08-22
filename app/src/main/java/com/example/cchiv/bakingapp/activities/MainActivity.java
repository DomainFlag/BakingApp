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

public class MainActivity extends AppCompatActivity implements MasterListFragment.onListItemListener {

    private static final String RECIPE_FRAGMENT_KEY = "recipeFragment";

    private RecipeFragment recipeFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            recipeFragment = (RecipeFragment) getSupportFragmentManager().getFragment(savedInstanceState, RECIPE_FRAGMENT_KEY);
        }
    }

    @Override
    public void onListItemClicked(Recipe recipe) {
        onRecipeSelect(recipe);
    }

    public void onRecipeSelect(Recipe recipe) {
        if(findViewById(R.id.recipe_detailed) != null) {
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
        } else {
            Intent intent = new Intent(this, RecipeActivity.class);
            intent.putExtra("id", recipe.getId());

            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(findViewById(R.id.recipe_detailed) != null && recipeFragment != null) {
            getSupportFragmentManager().putFragment(outState, RECIPE_FRAGMENT_KEY, recipeFragment);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(findViewById(R.id.recipe_detailed) != null && recipeFragment != null) {
            recipeFragment = (RecipeFragment) getSupportFragmentManager().getFragment(savedInstanceState, RECIPE_FRAGMENT_KEY);
        }
    }
}
