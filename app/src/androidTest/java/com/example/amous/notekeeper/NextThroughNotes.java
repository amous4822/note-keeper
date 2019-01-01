package com.example.amous.notekeeper;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.List;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class NextThroughNotes {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void nextNotes() {

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        onView(withId(R.id.list_item)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        List<NoteInfo> mTestNotes = DataManager.getInstance().getNotes();
        for (int index = 0; index<mTestNotes.size()-1; index++) {

            onView(withId(R.id.text_notebody)).check(matches(withText(mTestNotes.get(index).getText())));
            onView(withId(R.id.text_notetitle)).check(matches(withText(mTestNotes.get(index).getTitle())));
            onView(withId(R.id.spinner_editnote)).check(matches(withSpinnerText(mTestNotes.get(index).getCourse().getTitle())));

            onView(withId(R.id.action_next)).perform(click());

        }

        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));

    }

}