package com.example.cchiv.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.cchiv.bakingapp.obj.Recipe;

public class DetailedRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_recipe);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if(id == -1)
            finish();

        Recipe recipe = null;

        if(recipe == null)
            finish();

        RecipeFragment recipeFragment = new RecipeFragment();
        recipeFragment.onRecipeAttach(recipe);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.recipe_detailed, recipeFragment)
                .commit();
    }
}
