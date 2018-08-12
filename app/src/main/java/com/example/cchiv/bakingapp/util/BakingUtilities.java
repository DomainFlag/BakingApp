package com.example.cchiv.bakingapp.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.cchiv.bakingapp.data.ContentContract;
import com.example.cchiv.bakingapp.data.ContentContract.StepEntry;
import com.example.cchiv.bakingapp.data.ContentContract.IngredientEntry;
import com.example.cchiv.bakingapp.data.ContentContract.RecipeEntry;
import com.example.cchiv.bakingapp.obj.Ingredient;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.obj.Step;

import java.util.ArrayList;

public class BakingUtilities {

    private Context context;

    public BakingUtilities(Context context) {
        this.context = context;
    }

    public  Context getContext() {
        return this.context;
    }

    public ArrayList<Recipe> fetchRecipes() {
        Uri uri = ContentContract.BASE_CONTENT_URI.buildUpon().appendPath(ContentContract.PATH_RECIPES).build();

        Cursor cursor = getContext().getContentResolver().query(uri, new String[] {
                RecipeEntry._ID,
                RecipeEntry.COL_RECIPE_IMAGE,
                RecipeEntry.COL_RECIPE_SERVINGS,
                RecipeEntry.COL_RECIPE_NAME,
        }, null, null, null);

        ArrayList<Recipe> recipes = new ArrayList<>();

        int recipeIDIndex = cursor.getColumnIndexOrThrow(RecipeEntry._ID);
        int recipeImageIndex = cursor.getColumnIndexOrThrow(RecipeEntry.COL_RECIPE_IMAGE);
        int recipeNameIndex = cursor.getColumnIndexOrThrow(RecipeEntry.COL_RECIPE_NAME);
        int recipeServingsIndex = cursor.getColumnIndexOrThrow(RecipeEntry.COL_RECIPE_SERVINGS);

        while(cursor.moveToNext()) {
            recipes.add(new Recipe(
                    cursor.getInt(recipeIDIndex),
                    cursor.getString(recipeNameIndex),
                    fetchIngredients(cursor.getInt(recipeIDIndex)),
                    fetchSteps(cursor.getInt(recipeIDIndex)),
                    cursor.getInt(recipeServingsIndex),
                    cursor.getString(recipeImageIndex)
            ));
        }

        cursor.close();

        return recipes;
    }

    private ArrayList<Ingredient> fetchIngredients(int id) {
        Uri uri = ContentContract.BASE_CONTENT_URI.buildUpon().appendPath(ContentContract.PATH_INGREDIENTS).build();

        Cursor cursor = getContext().getContentResolver().query(uri, new String[] {
                IngredientEntry._ID,
                IngredientEntry.COL_INGREDIENT_RECIPE,
                IngredientEntry.COL_INGREDIENT_QUANTITY,
                IngredientEntry.COL_INGREDIENT_MEASURE,
                IngredientEntry.COL_INGREDIENT_INGREDIENT,
        }, null, new String[] { String.valueOf(id) }, null);

        ArrayList<Ingredient> ingredients = new ArrayList<>();

        int ingredientQuantityIndex = cursor.getColumnIndexOrThrow(IngredientEntry.COL_INGREDIENT_QUANTITY);
        int ingredientMeasureIndex = cursor.getColumnIndexOrThrow(IngredientEntry.COL_INGREDIENT_MEASURE);
        int ingredientIngredientIndex = cursor.getColumnIndexOrThrow(IngredientEntry.COL_INGREDIENT_INGREDIENT);

        while(cursor.moveToNext()) {
            ingredients.add(new Ingredient(
                    cursor.getFloat(ingredientQuantityIndex),
                    cursor.getString(ingredientMeasureIndex),
                    cursor.getString(ingredientIngredientIndex)
            ));
        }

        cursor.close();

        return ingredients;
    }

    private ArrayList<Step> fetchSteps(int id) {
        Uri uri = ContentContract.BASE_CONTENT_URI.buildUpon().appendPath(ContentContract.PATH_STEPS).build();

        Cursor cursor = getContext().getContentResolver().query(uri, new String[] {
                StepEntry._ID,
                StepEntry.COL_STEP_RECIPE,
                StepEntry.COL_STEP_VIDEO_URL,
                StepEntry.COL_STEP_THUMBNAIL_URL,
                StepEntry.COL_STEP_SHORT_DESCRIPTION,
                StepEntry.COL_STEP_DESCRIPTION,
        }, null, new String[] { String.valueOf(id) }, null);

        ArrayList<Step> steps = new ArrayList<>();

        int stepIndex = cursor.getColumnIndexOrThrow(StepEntry._ID);
        int stepVideoURLIndex = cursor.getColumnIndexOrThrow(StepEntry.COL_STEP_VIDEO_URL);
        int stepDescriptionIndex = cursor.getColumnIndexOrThrow(StepEntry.COL_STEP_DESCRIPTION);
        int stepShortDescriptionIndex = cursor.getColumnIndexOrThrow(StepEntry.COL_STEP_SHORT_DESCRIPTION);
        int stepThumbnailURLIndex = cursor.getColumnIndexOrThrow(StepEntry.COL_STEP_THUMBNAIL_URL);

        while(cursor.moveToNext()) {
            steps.add(new Step(
                    cursor.getInt(stepIndex),
                    cursor.getString(stepShortDescriptionIndex),
                    cursor.getString(stepDescriptionIndex),
                    cursor.getString(stepVideoURLIndex),
                    cursor.getString(stepThumbnailURLIndex)
            ));
        }

        cursor.close();

        return steps;
    }
}
