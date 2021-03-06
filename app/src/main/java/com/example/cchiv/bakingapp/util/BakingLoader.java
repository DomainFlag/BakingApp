package com.example.cchiv.bakingapp.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.example.cchiv.bakingapp.adapters.RecipeAdapter;
import com.example.cchiv.bakingapp.obj.Recipe;

import java.util.ArrayList;

public class BakingLoader implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {

    public interface OnInterfaceCallback {
        void OnInterfaceUpdateCallback(ArrayList<Recipe> recipes);
    }

    public interface OnInterfaceRecipeCallback {
        void OnInterfaceRecipeUpdateCallback(Recipe recipe);
    }

    private OnInterfaceCallback onInterfaceCallback = null;
    private OnInterfaceRecipeCallback onInterfaceRecipeCallback = null;

    private int recipeId;

    private Context context;
    private RecipeAdapter recipeAdapter = null;

    public BakingLoader(Context context) {
        this.context = context;
        this.onInterfaceCallback = (OnInterfaceCallback) context;
    };

    public BakingLoader(Context context, OnInterfaceRecipeCallback onInterfaceRecipeCallback, int recipeId) {
        this.context = context;
        this.onInterfaceRecipeCallback = onInterfaceRecipeCallback;
        this.recipeId = recipeId;
    }

    public BakingLoader(Context context, RecipeAdapter recipeAdapter) {
        this.context = context;
        this.recipeAdapter = recipeAdapter;
    }

    @NonNull
    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new RecipeAsyncTaskLoader(context);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> recipes) {
        if(recipeAdapter == null) {
            if(onInterfaceCallback != null) {
                onInterfaceCallback.OnInterfaceUpdateCallback(recipes);
            }

            if(onInterfaceRecipeCallback != null) {
                Recipe recipe = BakingUtilities.getRecipe(recipes, this.recipeId);

                onInterfaceRecipeCallback.OnInterfaceRecipeUpdateCallback(recipe);
            }
        } else {
            recipeAdapter.swapContent(recipes);
            recipeAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Recipe>> loader) {}

    public static class RecipeAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Recipe>> {

        public RecipeAsyncTaskLoader(@NonNull Context context) {
            super(context);
        }

        @Nullable
        @Override
        public ArrayList<Recipe> loadInBackground() {
            BakingUtilities bakingUtilities = new BakingUtilities(getContext());
            return bakingUtilities.fetchRecipes();
        }
    }
}
