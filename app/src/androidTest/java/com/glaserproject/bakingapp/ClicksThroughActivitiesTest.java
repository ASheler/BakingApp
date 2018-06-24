package com.glaserproject.bakingapp;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class ClicksThroughActivitiesTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private String recipeName = "Brownies";
    private String stepName = "Recipe Introduction";

    //matcher initiator for resource name
    public static Matcher<View> withResourceName(String resourceName) {
        return withResourceName(CoreMatchers.is(resourceName));
    }

    //matcher
    public static Matcher<View> withResourceName(final Matcher<String> resourceNameMatcher) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with resource name: ");
                resourceNameMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                int id = view.getId();
                return id != View.NO_ID && id != 0 && view.getResources() != null
                        && resourceNameMatcher.matches(view.getResources().getResourceName(id));
            }
        };
    }

    @Test
    public void selectSecondRecipe() {
        if (getRVcount() == 0) {
            //waitTime until RV loads
            //rv adapter is first init empty for ClickListener -> espresso won't wait for it
            waitTime();
        }


        //click on item in rv with recipe name @recipeName
        clickOnItem(R.id.recipe_list_rv, recipeName);

        //wait till RV loads
        waitTime();

        //check if actionBar title is the same as clicked item
        onView(allOf(isDescendantOfA(withResourceName("android:id/action_bar_container")), withText(recipeName)));

        clickOnItem(R.id.step_selection_rv, stepName);

        waitTime();

        onView(allOf(withId(R.id.description_tv), withText(stepName)));


    }

    private void clickOnItem(int rvId, String recipeName) {
        //click on rv item, where descendant has @recipeName in it
        onView(withId(rvId)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(recipeName)), click()));


    }

    //get ItemCount on rv so we can check if it has items in it
    private int getRVcount() {
        RecyclerView recyclerView = mActivityTestRule.getActivity().findViewById(R.id.recipe_list_rv);
        return recyclerView.getAdapter().getItemCount();
    }

    private void waitTime() {
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
