package com.example.cchiv.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.util.BakingLoader;

import java.util.ArrayList;

public class DetailedRecipeActivity extends AppCompatActivity implements BakingLoader.OnInterfaceCallback {

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
        int id = intent.getIntExtra("id", 0);

        if(id == -1 || id >= recipes.size())
            finish();

        Recipe recipe = recipes.get(id);

        RecipeFragment recipeFragment = new RecipeFragment();
        recipeFragment.onRecipeAttach(recipe);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.recipe_detailed, recipeFragment)
                .commit();

    }
}
