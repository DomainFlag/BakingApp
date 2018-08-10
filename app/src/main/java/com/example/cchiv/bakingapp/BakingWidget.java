package com.example.cchiv.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.cchiv.bakingapp.obj.Recipe;
import com.example.cchiv.bakingapp.util.BakingParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidget extends AppWidgetProvider {

    public final static String PREVIOUS_STEP = "PREVIOUS_STEP";
    public final static String NEXT_STEP = "NEXT_STEP";
    public final static String UPDATE_RECIPE = "UPDATE_RECIPE";

    public final static int PREVIOUS_STEP_CODE = 21;
    public final static int NEXT_STEP_CODE = 22;

    private static int mStepIndex = 0;
    private static ArrayList<Recipe> recipes;

    private final static String TAG = "BakingWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        BakingParser bakingParser = new BakingParser(context);
        StringBuilder stringBuilder = bakingParser.readBackingSource();
        recipes = bakingParser.parseBackingSource(stringBuilder);

        Recipe recipe = recipes.get(mStepIndex);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context, NEXT_STEP_CODE,
                createStepIntent(context, NEXT_STEP), PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.next_widget_step, pendingNextIntent);


        PendingIntent pendingPreviousIntent = PendingIntent.getBroadcast(context, PREVIOUS_STEP_CODE,
                createStepIntent(context, PREVIOUS_STEP), PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.previous_widget_step, pendingPreviousIntent);

        renderRecipe(appWidgetManager, views, recipe, appWidgetId);
    }

    public static void renderRecipe(AppWidgetManager appWidgetManager, RemoteViews views, Recipe recipe, int appWidgetId) {
        Picasso.get().load(recipe.getSteps().get(mStepIndex).getVideoURL()).into(views, R.id.widget_thumbnail, new int[] { appWidgetId });
        views.setTextViewText(R.id.widget_content, recipe.getSteps().get(mStepIndex).getDescription());

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static Intent createStepIntent(Context context, String type) {
        Intent intent = new Intent(context, BakingWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra("type", type);

        return intent;
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        if(intent.getStringExtra("type") == null)
            return;

        Recipe recipe = recipes.get(0);

        int appWidgetId = AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context, BakingWidget.class))[0];

        if(intent.getStringExtra("type").equals(UPDATE_RECIPE)) {
            int id = intent.getIntExtra("id", -1);

            if(id == -1)
                return;

            recipe = recipes.get(id);
            mStepIndex = 0;
        } else {
            if(intent.getStringExtra("type").equals(NEXT_STEP)) {
                if(mStepIndex < recipe.getSteps().size()-1)
                    mStepIndex++;
            } else {
                if(mStepIndex > 0)
                    mStepIndex--;
            }
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        renderRecipe(appWidgetManager, views, recipe, appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

