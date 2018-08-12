package com.example.cchiv.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.cchiv.bakingapp.data.ContentContract;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.util.BakingLoader;
import com.example.cchiv.bakingapp.util.BakingUtilities;

import java.util.ArrayList;

public class BakingWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingRemoteViewsFactory(this.getApplicationContext());
    }

    class BakingRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, BakingLoader.RecipeAsyncTaskLoader.OnLoadCompleteListener<ArrayList<Recipe>>{

        private Context context;

        private Cursor cursor = null;

        BakingRemoteViewsFactory(Context context) {
            this.context = context;
        }

        @Override
        public void onLoadComplete(@NonNull Loader<ArrayList<Recipe>> loader, @Nullable
                ArrayList<Recipe> recipes) {

        }

        @Override
        public void onCreate() {}

        @Override
        public void onDataSetChanged() {
            Uri uri = ContentContract.BASE_CONTENT_URI.buildUpon().appendPath(ContentContract.PATH_RECIPES).build();
            if(cursor != null) cursor.close();
            Cursor cursor = context.getContentResolver().query(uri, new String[] {
                    ContentContract.RecipeEntry.COL_RECIPE_NAME,
                    ContentContract.RecipeEntry.COL_RECIPE_SERVINGS,
                    ContentContract.RecipeEntry.COL_RECIPE_IMAGE
            }, null, null, null);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if(cursor != null) return cursor.getCount();
            else return 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            BakingUtilities bakingUtilities = new BakingUtilities(context);
            ArrayList<Recipe> recipes =  bakingUtilities.fetchRecipes();

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.recipe_layout);

            remoteViews.setTextViewText(R.id.mTextViewName, recipes.get(0).getName());

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
