package com.michaelhsieh.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

public class RecipeIngredientsDisplayService {

    /**
     * Update recipe widget ingredients list to match the selected Recipe
     */
    public static void handleActionUpdateRecipeWidgets(Context context, String recipeName, String ingredients)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context, RecipeWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);

        // Trigger the data update to handle the widget ingredient text and force a data refresh
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_text);

        // Update all widgets
        RecipeWidgetProvider.updateRecipeWidgets(context, appWidgetManager,
                recipeName, ingredients, appWidgetIds);
    }
}
