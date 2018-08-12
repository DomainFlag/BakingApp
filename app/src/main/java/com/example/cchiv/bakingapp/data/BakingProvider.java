package com.example.cchiv.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BakingProvider extends ContentProvider {

    private static final int RECIPES_SINGLE = 70;
    private static final int RECIPES_MANY = 71;
    private static final int INGREDIENTS_MANY = 73;
    private static final int STEPS_MANY = 75;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_RECIPES, RECIPES_MANY);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_RECIPES + "/#", RECIPES_SINGLE);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_INGREDIENTS, INGREDIENTS_MANY);
        uriMatcher.addURI(ContentContract.AUTHORITY, ContentContract.PATH_STEPS, STEPS_MANY);

        return uriMatcher;
    }

    private ContentDbHelper contentDbHelper;

    @Override
    public boolean onCreate() {
        contentDbHelper = new ContentDbHelper(getContext());

        contentDbHelper.getWritableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String orderBy) {
        SQLiteDatabase sqLiteDatabase = contentDbHelper.getReadableDatabase();
        Cursor cursor;

        switch(uriMatcher.match(uri)) {
            case RECIPES_SINGLE : {
                String id = uri.getPathSegments().get(1);
                cursor = sqLiteDatabase.query(ContentContract.RecipeEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        new String[] { id },
                        null,
                        null,
                        orderBy);
                break;
            }
            case RECIPES_MANY : {
                cursor = sqLiteDatabase.query(ContentContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        orderBy);
                break;
            }
            case INGREDIENTS_MANY : {
                cursor = sqLiteDatabase.query(ContentContract.IngredientEntry.TABLE_NAME,
                        projection,
                        "idR=?",
                        selectionArgs,
                        null,
                        null,
                        orderBy);
                break;
            }
            case STEPS_MANY : {
                cursor = sqLiteDatabase.query(ContentContract.StepEntry.TABLE_NAME,
                        projection,
                        "idR=?",
                        selectionArgs,
                        null,
                        null,
                        orderBy);
                break;
            }
            default: throw new UnsupportedOperationException("Unsupported operation");
        }

        if(getContext() != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
