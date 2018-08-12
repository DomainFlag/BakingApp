package com.example.cchiv.bakingapp.obj;

import android.content.ContentValues;

import com.example.cchiv.bakingapp.data.ContentContract.IngredientEntry;

public class Ingredient {

    private float quantity;
    private String measure;
    private String ingredient;

    public Ingredient(float quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public ContentValues generateContentValues(long recipeID) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(IngredientEntry.COL_INGREDIENT_RECIPE, recipeID);
        contentValues.put(IngredientEntry.COL_INGREDIENT_INGREDIENT, this.ingredient);
        contentValues.put(IngredientEntry.COL_INGREDIENT_MEASURE, this.measure);
        contentValues.put(IngredientEntry.COL_INGREDIENT_QUANTITY, this.quantity);

        return contentValues;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
