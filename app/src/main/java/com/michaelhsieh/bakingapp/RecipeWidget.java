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

        // TODO: open up MainActivity when widget first created
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

        // When the first widget is created, set default text and allow
        // user to open up MainActivity when clicked

        // this will also reset the ingredients text to default text every 30 minutes
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, "", "", appWidgetId);
        }
        Log.d(TAG,"onUpdate");

        // don't update widgets in onUpdate because we only update widgets when user
        // selects a recipe
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

    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

