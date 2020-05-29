package com.michaelhsieh.bakingapp;

import android.view.View;

import com.michaelhsieh.bakingapp.model.Recipe;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;

/**
 * This test demos a user clicking on a RecyclerView item in MainActivity which opens up the
 * corresponding DetailActivity.
 *
 * This test utilizes Idling Resources because there is a delay when recipe data is being
 * retrieved from the Web.
 */
@RunWith(AndroidJUnit4.class)
public class IdlingResourceMainActivityTest {
    private static final String RECIPE_NAME = "Nutella Pie";
    private static final String RECIPE_STEP = "Recipe Introduction";
    private static final String STEP_DESC = "Recipe Introduction";

    /**
     * The ActivityTestRule is a rule provided by Android used for functional testing of a single
     * activity. The activity that will be tested, MenuActivity in this case, will be launched
     * before each test that's annotated with @Test and before methods annotated with @Before.
     *
     * The activity will be terminated after the test and methods annotated with @After are
     * complete. This rule allows you to directly access the activity during the test.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void idlingResourceTest() {
        //onView(withId(R.id.rv_recipes))
        //        .check(matches(hasDescendant(withText(RECIPE_NAME))));

        // check the name of a recipe card and click on that recipe card
        onView(withId(R.id.rv_recipes))
                .perform(scrollToPosition(0))
                .check(matches(atPosition(0, hasDescendant(withText(RECIPE_NAME)))));
        onView(withId(R.id.rv_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // check the short description of a recipe step and click the recipe step
        onView(withId(R.id.rv_recipe_steps))
                .perform(scrollToPosition(0))
                .check(matches(atPosition(0, hasDescendant(withText(RECIPE_STEP)))));
        onView(withId(R.id.rv_recipe_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // then check the recipe step's full description
        onView(withId(R.id.tv_step_long_desc)).check(matches(withText(STEP_DESC)));

        // onData(anything()).inAdapterView(withId(R.id.rv_recipes)).atPosition(0).perform(click());
        //onView(withId(R.id.tea_name_text_view)).check(matches(withText(TEA_NAME)));
    }

    // Remember to unregister resources when not needed to avoid malfunction.
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

    /** Method to test item in RecyclerView at a given position.
     *
     */
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
