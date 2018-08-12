package com.example.cchiv.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cchiv.bakingapp.data.ContentContract.IngredientEntry;
import com.example.cchiv.bakingapp.data.ContentContract.RecipeEntry;
import com.example.cchiv.bakingapp.data.ContentContract.StepEntry;
import com.example.cchiv.bakingapp.obj.Ingredient;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.obj.Step;
import com.example.cchiv.bakingapp.util.BakingParser;

import java.util.ArrayList;

public class ContentDbHelper extends SQLiteOpenHelper {

    private final static String TAG = "ContentDbHelper";

    private final static String DATABASE_NAME = "baking.db";

    private Context context;

    public ContentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlRecipeQuery = "CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
                RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                RecipeEntry.COL_RECIPE_IMAGE + " TEXT, " +
                RecipeEntry.COL_RECIPE_NAME + " TEXT NOT NULL, " +
                RecipeEntry.COL_RECIPE_SERVINGS + " INTEGER NOT NULL " +
        ")";

        sqLiteDatabase.execSQL(sqlRecipeQuery);

        String sqlIngredientQuery = "CREATE TABLE " + IngredientEntry.TABLE_NAME + " (" +
                IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IngredientEntry.COL_INGREDIENT_RECIPE + " INTEGER REFERENCES " +
                    RecipeEntry.TABLE_NAME + "(" + RecipeEntry._ID + ")" + ", " +

                IngredientEntry.COL_INGREDIENT_INGREDIENT + " TEXT NOT NULL, " +
                IngredientEntry.COL_INGREDIENT_MEASURE + " REAL, " +
                IngredientEntry.COL_INGREDIENT_QUANTITY + " INTEGER " +
                ")";

        sqLiteDatabase.execSQL(sqlIngredientQuery);


        String sqlStepQuery = "CREATE TABLE " + StepEntry.TABLE_NAME + " (" +
                StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StepEntry.COL_STEP_RECIPE + " INTEGER REFERENCES " +
                    RecipeEntry.TABLE_NAME + "(" + RecipeEntry._ID + ")" + ", " +

                StepEntry.COL_STEP_DESCRIPTION + " TEXT NOT NULL, " +
                StepEntry.COL_STEP_SHORT_DESCRIPTION + " TEXT NOT NULL, " +
                StepEntry.COL_STEP_THUMBNAIL_URL + " TEXT, " +
                StepEntry.COL_STEP_VIDEO_URL + " TEXT " +
                ")";

        sqLiteDatabase.execSQL(sqlStepQuery);

        insertDummyData(sqLiteDatabase);
    }

    private void insertDummyData(SQLiteDatabase sqLiteDatabase) {
        BakingParser bakingParser = new BakingParser(context);
        StringBuilder bakingStringData = bakingParser.readBackingSource();
        ArrayList<Recipe> recipes = bakingParser.parseBackingSource(bakingStringData);

        for(Recipe recipe : recipes) {
            long rowRecipeID = sqLiteDatabase.insert(RecipeEntry.TABLE_NAME, null, recipe.generateContentValues());

            for(Ingredient ingredient : recipe.getIngredients()) {
                long rowIngredientID = sqLiteDatabase.insert(IngredientEntry.TABLE_NAME, null, ingredient.generateContentValues(rowRecipeID));
            }

            for(Step step : recipe.getSteps()) {
                long rowStepID = sqLiteDatabase.insert(StepEntry.TABLE_NAME, null, step.generateContentValues(rowRecipeID));
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
