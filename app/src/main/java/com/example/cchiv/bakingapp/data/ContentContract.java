package com.example.cchiv.bakingapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ContentContract {

    public static final String AUTHORITY = "com.example.android.BakingProvider";

    public static final String PATH_RECIPES = "recipes";
    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String PATH_STEPS = "steps";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public ContentContract() {}

    public static final class RecipeEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        public static final String TABLE_NAME = "recipe";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_RECIPE_IMAGE = "image";
        public static final String COL_RECIPE_NAME = "name";
        public static final String COL_RECIPE_SERVINGS = "servings";
    }

    public static final class IngredientEntry implements BaseColumns {
        public static final String TABLE_NAME = "ingredient";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_INGREDIENT_RECIPE = "idR";
        public static final String COL_INGREDIENT_QUANTITY = "quantity";
        public static final String COL_INGREDIENT_MEASURE = "measure";
        public static final String COL_INGREDIENT_INGREDIENT = "ingredient";
    }

    public static final class StepEntry implements BaseColumns {
        public static final String TABLE_NAME = "step";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_STEP_RECIPE = "idR";
        public static final String COL_STEP_SHORT_DESCRIPTION = "shortDescription";
        public static final String COL_STEP_DESCRIPTION = "description";
        public static final String COL_STEP_VIDEO_URL = "videoURL";
        public static final String COL_STEP_THUMBNAIL_URL = "thumbnailURL";
    }
}
