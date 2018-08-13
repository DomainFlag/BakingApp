package com.example.cchiv.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.util.BakingUtilities;

import java.util.ArrayList;

public class BakingWidgetService extends RemoteViewsService {

    public static final String TAG = "BakingWidgetService";

    private ArrayList<Recipe> recipes = null;

    private Recipe recipe = null;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class BakingRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

        private Context context;

        private int recipeId;

        BakingRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
            this.recipeId = intent.getIntExtra("id", -1);
        }

        @Override
        public void onCreate() {}

        @Override
        public void onDataSetChanged() {
            BakingUtilities bakingUtilities = new BakingUtilities(context);
            recipes = bakingUtilities.fetchRecipes();

            for(int it = 0; it < recipes.size(); it++) {
                if(recipes.get(it).getId() == recipeId)
                    recipe = recipes.get(it);
            }
        }

        @Override
        public void onDestroy() {}

        @Override
        public int getCount() {
            if(recipe != null) return recipe.getIngredients().size() + 1;
            else return 1;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews remoteViews;

            if(i == 0) {
                remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_label_layout);
                remoteViews.setTextViewText(R.id.widget_label_layout, "Ingredients");
            } else if(recipe != null) {
                String ingredient = recipe.getIngredients().get(i-1).getIngredient();

                remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_recipe_layout);
                remoteViews.setTextViewText(R.id.widget_recipe_name, ingredient);
            } else return null;

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
