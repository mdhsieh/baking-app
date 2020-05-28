package com.michaelhsieh.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 *
 * Displays the ingredients list of the user's selected recipe.
 */
public class RecipeWidget extends AppWidgetProvider {

    private static final String TAG = RecipeWidget.class.getSimpleName();

    /* static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) { */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                String recipeName, String ingredients, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        //CharSequence widgetText = context.getString(R.string.default_ingredients_list_text);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        // if no recipe was selected yet, don't set the widget text
        // set the widget text to display the selected recipe's ingredients
        if (!recipeName.isEmpty())
        {
            String widgetText = recipeName + "\n" + "\n" + ingredients;
            views.setTextViewText(R.id.appwidget_text, widgetText);
            Log.d(TAG, "set widget text to: " + widgetText);
        }
        else
        {
            Log.d(TAG, "no recipe selected");
        }

        // Create an Intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        /*for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        } */

        // When the first widget is created, allow
        // user to open up MainActivity when clicked
        for (int appWidgetId : appWidgetIds) {

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Get the layout for the Recipe Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

            // don't use RecipeWidget's updateAppWidget in onUpdate because
            // we only update widgets when user selects a recipe
            //updateAppWidget(context, appWidgetManager, "", "", appWidgetId);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        //Log.d(TAG,"onUpdate");
    }

    /**
     * Updates all widget instances given the widget Ids and display information
     *
     * @param context          The calling context
     * @param appWidgetManager The widget manager
     * @param recipeName       The name of the selected recipe
     * @param ingredients      The ingredients of that recipe
     * @param appWidgetIds     Array of widget Ids to be updated
     */
    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager,
                                          String recipeName, String ingredients, int[] appWidgetIds)
    {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeName, ingredients, appWidgetId);
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

