package com.example.cchiv.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.cchiv.bakingapp.fragments.MasterListFragment;
import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.fragments.RecipeFragment;
import com.example.cchiv.bakingapp.obj.Recipe;

public class MainActivity extends AppCompatActivity implements MasterListFragment.onListItemListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onListItemClicked(Recipe recipe) {
        onRecipeSelect(recipe);
    }

    public void onRecipeSelect(Recipe recipe) {
        if(findViewById(R.id.recipe_detailed) != null) {
            RecipeFragment recipeFragment = new RecipeFragment();
            recipeFragment.onRecipeAttach(recipe);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.recipe_detailed, recipeFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeActivity.class);
            intent.putExtra("id", recipe.getId());

            startActivity(intent);
        }
    }
}
