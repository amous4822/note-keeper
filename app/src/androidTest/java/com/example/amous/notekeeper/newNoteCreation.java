package com.example.amous.notekeeper;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class newNoteCreation {

/*
    private DataManager mDataManager;

   @BeforeClass
    public void setUp(){
        mDataManager = DataManager.getInstance();
    }
*/

    @Rule
    public ActivityTestRule<NoteListActivity> mNoteListActivity =
            new ActivityTestRule<>(NoteListActivity.class);

    @Test
    public void createNewNote() {

        String testBody = "testing note body";
        String testTitle = "testing note title";
        CourseInfo mTestCourse = DataManager.getInstance().getCourse("java_lang");

        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.spinner_editnote)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class), equalTo(mTestCourse))).perform(click());

        onView(withId(R.id.text_notebody)).perform(typeText(testBody)).check(matches(withText(testBody)));
        onView(withId(R.id.text_notetitle)).perform(typeText(testTitle), closeSoftKeyboard());

        pressBack();


        int lastNote = DataManager.getInstance().getNotes().size() - 1;
        NoteInfo mTestNotes = DataManager.getInstance().getNotes().get(lastNote);

        onView(withId(R.id.list_note)).perform(RecyclerViewActions.actionOnItemAtPosition(lastNote, click()));

        onView(withId(R.id.text_notebody)).check(matches(withText(mTestNotes.getText())));
        onView(withId(R.id.text_notetitle)).check(matches(withText(mTestNotes.getTitle())));
        onView(withId(R.id.spinner_editnote)).check(matches(withSpinnerText(mTestNotes.getCourse().getTitle())));

    }

}
