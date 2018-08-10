package com.example.cchiv.bakingapp.util;

import android.content.Context;
import android.util.Log;

import com.example.cchiv.bakingapp.R;
import com.example.cchiv.bakingapp.obj.Ingredient;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.obj.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BakingParser {

    private static ArrayList<Recipe> recipes = new ArrayList<>();

    private static final String TAG = "BakingParser";

    private Context context;

    public BakingParser(Context context) {
        this.context = context;
    }

    public StringBuilder readBackingSource() {
        InputStream inputStream = this.context.getResources().openRawResource(R.raw.baking);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line = bufferedReader.readLine();
            while(line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        return stringBuilder;
    }

    public static ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public static Recipe getRecipe(int id) {
        for(int it = 0; it < recipes.size(); it++) {
            Recipe recipe = recipes.get(it);

            if(recipe.getId() == id)
                return recipe;
        }

        return null;
    }

    public ArrayList<Recipe> parseBackingSource(StringBuilder stringBuilder) {
        recipes.clear();

        String recipesString = stringBuilder.toString();
        if(recipesString.isEmpty())
            return recipes;

        try {
            JSONArray jsonRecipesArray = new JSONArray(recipesString);

            for(int it = 0; it < jsonRecipesArray.length(); it++) {
                JSONObject jsonRecipeObject = jsonRecipesArray.getJSONObject(it);

                int id = jsonRecipeObject.optInt("id");
                String name = jsonRecipeObject.optString("name");

                JSONArray jsonIngredientsArray = jsonRecipeObject.optJSONArray("ingredients");
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                for(int iti = 0; iti < jsonIngredientsArray.length(); iti++) {
                    JSONObject jsonIngredientObject = jsonIngredientsArray.getJSONObject(iti);

                    ingredients.add(new Ingredient(
                            (float) jsonIngredientObject.optDouble("quantity"),
                            jsonIngredientObject.optString("measure"),
                            jsonIngredientObject.optString("ingredient")
                    ));
                };

                JSONArray jsonStepsArray = jsonRecipeObject.optJSONArray("steps");
                ArrayList<Step> steps = new ArrayList<>();
                for(int iti = 0; iti < jsonStepsArray.length(); iti++) {
                    JSONObject jsonStepObject = jsonStepsArray.getJSONObject(iti);

                    steps.add(new Step(
                            jsonStepObject.optInt("id"),
                            jsonStepObject.optString("shortDescription"),
                            jsonStepObject.optString("description"),
                            jsonStepObject.optString("videoURL"),
                            jsonStepObject.optString("thumbnailURL")
                    ));
                };

                int recipe = jsonRecipeObject.optInt("servings");
                String image = jsonRecipeObject.optString("image");

                recipes.add(new Recipe(
                        id,
                        name,
                        ingredients,
                        steps,
                        recipe,
                        image
                ));
            }
        } catch(JSONException e) {
            Log.v(TAG, e.toString());
        }

        return recipes;
    }
}
