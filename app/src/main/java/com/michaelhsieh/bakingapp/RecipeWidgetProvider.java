package com.michaelhsieh.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 *
 * Displays the ingredients list of the user's selected recipe.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    private static final String TAG = RecipeWidgetProvider.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                String recipeName, String ingredients, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        // if no recipe was selected yet, don't set the widget text
        // set the widget text to display the selected recipe's ingredients
        if (!recipeName.isEmpty())
        {
            String widgetText = recipeName + "\n" + "\n" + ingredients;
            views.setTextViewText(R.id.appwidget_text, widgetText);

            Log.d(TAG, "widget text: " + widgetText);
        }
        else
        {
            Log.d(TAG, "no recipes selected");
        }

        // Create an Intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        /* Clear all previous Activities in the current task. This is done to
        present the user with a new recipe cards screen that isn't stacked on top of
        any previously opened Activities. If the user presses the back button, he/she should
        immediately be brought back to the home screen.
         */
        /* Intent intent = new Intent(this, A.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        CurrentActivity.this.finish();

        This will clear the stack */
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        // There may be multiple widgets active, so update all of them

        // When the first widget is created, allow
        // user to open up MainActivity when clicked
        for (int appWidgetId : appWidgetIds) {

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            /* Clear all previous Activities in the current task. This is done to
            present the user with a new recipe cards screen that isn't stacked on top of
            any previously opened Activities. If the user presses the back button, he/she should
            immediately be brought back to the home screen.
             */
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            // Get the layout for the Recipe Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

            // don't use RecipeWidgetProvider's updateAppWidget in onUpdate because
            // we only update widgets when user selects a recipe

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
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

