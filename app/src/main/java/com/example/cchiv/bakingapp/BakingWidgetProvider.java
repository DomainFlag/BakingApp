package com.example.cchiv.bakingapp;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.cchiv.bakingapp.callbacks.OnWidgetInterfaceUpdater;
import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.obj.Step;
import com.example.cchiv.bakingapp.util.BakingUtilities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    public final static String PREVIOUS_STEP = "PREVIOUS_STEP";
    public final static String NEXT_STEP = "NEXT_STEP";
    public final static String UPDATE_RECIPE = "UPDATE_RECIPE";
    public final static String TOGGLE_MENU = "TOGGLE_MENU";

    public final static int PREVIOUS_STEP_CODE = 21;
    public final static int NEXT_STEP_CODE = 22;
    public final static int TOGGLE_MENU_CODE = 23;

    private static boolean mMenuToggle = false;
    private static int mStepIndex = 0;
    private final static ArrayList<Recipe> recipes = new ArrayList<>();

    private static Recipe recipe = null;

    private final static String TAG = "BakingWidgetProvider";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        if(recipe == null)
            recipe = recipes.get(mStepIndex);

        // Construct the RemoteViews object
        RemoteViews views;

        if(Build.VERSION.SDK_INT >= 16) {
            Bundle bundle = appWidgetManager.getAppWidgetOptions(appWidgetId);
            int width = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);

            views = updateWidgetViews(context, appWidgetId, width);
        } else {
            views = updateWidgetViews(context, appWidgetId, -1);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static RemoteViews updateWidgetViews(Context context, int appWidgetId, int width) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        if(width < 300 && width != -1) {
            views.setViewVisibility(R.id.widget_next_label, View.GONE);
            views.setViewVisibility(R.id.widget_previous_label, View.GONE);
            views.setViewVisibility(R.id.widget_menu_toggle, View.GONE);
            mMenuToggle = false;
        } else {
            views.setViewVisibility(R.id.widget_next_label, View.VISIBLE);
            views.setViewVisibility(R.id.widget_previous_label, View.VISIBLE);
            views.setViewVisibility(R.id.widget_menu_toggle, View.VISIBLE);
        }

        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context, NEXT_STEP_CODE,
                createStepIntent(context, NEXT_STEP, appWidgetId), PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.next_widget_step, pendingNextIntent);

        PendingIntent pendingPreviousIntent = PendingIntent.getBroadcast(context, PREVIOUS_STEP_CODE,
                createStepIntent(context, PREVIOUS_STEP, appWidgetId), PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.previous_widget_step, pendingPreviousIntent);

        PendingIntent onToggleMenuIntent = PendingIntent.getBroadcast(context, TOGGLE_MENU_CODE,
                createStepIntent(context, TOGGLE_MENU, appWidgetId), PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_menu_toggle, onToggleMenuIntent);

        if(!mMenuToggle) {
            views.setViewVisibility(R.id.widget_ingredients, View.GONE);
            views.setInt(R.id.widget_menu_toggle, "setImageAlpha", 80);
        } else {
            views.setViewVisibility(R.id.widget_ingredients, View.VISIBLE);
            views.setInt(R.id.widget_menu_toggle, "setImageAlpha", 255);
        }

        Step step = recipe.getSteps().get(mStepIndex);
        if(mStepIndex == 0) {
            views.setViewVisibility(R.id.previous_widget_step, View.GONE);
        } else if(mStepIndex == recipe.getSteps().size()-1) {
            views.setViewVisibility(R.id.next_widget_step, View.GONE);
        } else {
            views.setViewVisibility(R.id.previous_widget_step, View.VISIBLE);
            views.setViewVisibility(R.id.next_widget_step, View.VISIBLE);
        }

        views.setTextViewText(R.id.widget_recipe_name, recipe.getName());
        views.setTextViewText(R.id.widget_short_description, step.getShortDescription());
        views.setTextViewText(R.id.widget_description, step.getDescription());

        Intent intent = new Intent(context, BakingWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra("id", recipe.getId());
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(appWidgetId, R.id.widget_ingredients, intent);

        return views;
    }

    public static Intent createStepIntent(Context context, String type, int appWidgetId) {
        Intent intent = new Intent(context, BakingWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId });
        intent.putExtra("type", type);

        return intent;
    };

    @Override
    public void onReceive(Context context, final Intent intent) {
        if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED) ||
                intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) &&
                recipes.size() == 0) {
            WidgetAsyncTask widgetAsyncTask = new WidgetAsyncTask(context, new OnWidgetInterfaceUpdater() {
                @Override
                public void OnUpdateWidgetInterface(Context context, ArrayList<Recipe> fetchedRecipes) {
                    recipes.clear();
                    recipes.addAll(fetchedRecipes);

                    Intent intent = new Intent(context, BakingWidgetProvider.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

                    int[] ids = AppWidgetManager.getInstance(context.getApplicationContext())
                            .getAppWidgetIds(new ComponentName(context.getApplicationContext(), BakingWidgetProvider.class));
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

                    context.sendBroadcast(intent);
                }
            });
            widgetAsyncTask.execute();
        }

        if(recipes.size() == 0)
            return;

        if(intent.getStringExtra("type") == null) {
            mStepIndex = 0;
            recipe = recipes.get(0);
        } else {
            switch(intent.getStringExtra("type")) {
                case UPDATE_RECIPE : {
                    int id = intent.getIntExtra("id", -1);

                    mStepIndex = 0;
                    recipe = recipes.get(0);

                    for (int it = 0; it < recipes.size(); it++)
                        if (recipes.get(it).getId() == id)
                            recipe = recipes.get(it);

                    break;
                }
                case TOGGLE_MENU : {
                    mMenuToggle = !mMenuToggle;

                    break;
                }
                case NEXT_STEP : {
                    if (mStepIndex < recipe.getSteps().size() - 1) {
                        mStepIndex++;
                    }

                    break;
                }
                case PREVIOUS_STEP : {
                    if (mStepIndex > 0) {
                        mStepIndex--;
                    }

                    break;
                }
                default : {
                    Log.v(TAG, "Unknown operation " + intent.getStringExtra("type"));
                }
            }
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int
            appWidgetId, Bundle newOptions) {
        int width = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);

        RemoteViews views = updateWidgetViews(context, appWidgetId, width);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static class WidgetAsyncTask extends AsyncTask<Void, Void, ArrayList<Recipe>> {

        private OnWidgetInterfaceUpdater onWidgetInterfaceUpdater;
        private WeakReference<Context> contextWeakReference;

        private WidgetAsyncTask(Context context, OnWidgetInterfaceUpdater onWidgetInterfaceUpdater) {
            this.contextWeakReference = new WeakReference<>(context);
            this.onWidgetInterfaceUpdater = onWidgetInterfaceUpdater;
        }

        @Override
        protected ArrayList<Recipe> doInBackground(Void... voids) {
            if(contextWeakReference.get() != null) {
                BakingUtilities bakingUtilities = new BakingUtilities(contextWeakReference.get());
                return bakingUtilities.fetchRecipes();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Recipe> recipes) {
            super.onPostExecute(recipes);

            if(recipes != null && contextWeakReference.get() != null) {
                this.onWidgetInterfaceUpdater.OnUpdateWidgetInterface(
                        contextWeakReference.get(),
                        recipes);
            }
        }
    }
}

