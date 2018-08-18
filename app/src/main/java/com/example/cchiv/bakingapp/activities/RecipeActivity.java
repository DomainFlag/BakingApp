package com.example.cchiv.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.cchiv.bakingapp.fragments.MasterListFragment;
import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.fragments.RecipeFragment;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.util.BakingLoader;
import com.example.cchiv.bakingapp.util.BakingUtilities;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity implements BakingLoader.OnInterfaceCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_recipe);

        BakingLoader bakingLoader = new BakingLoader(this);
        getSupportLoaderManager().initLoader(MasterListFragment.LOADER_RECIPES, null, bakingLoader).forceLoad();;
    }

    @Override
    public void OnInterfaceUpdateCallback(ArrayList<Recipe> recipes) {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if(id == -1) finish();

        Recipe recipe = BakingUtilities.getRecipe(recipes, id);

        RecipeFragment recipeFragment = new RecipeFragment();
        recipeFragment.onRecipeAttach(recipe);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.recipe_detailed, recipeFragment)
                .commit();

    }
}
