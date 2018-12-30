package com.example.amous.notekeeper;

import android.provider.ContactsContract;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    public static DataManager dm;

    @BeforeClass
    public static void classSetUp(){

        dm =DataManager.getInstance();
    }

    @Before
    public void setUp(){

        dm.getNotes().clear();
        dm.initializeExampleNotes();
    }

    @Test
    public void createNewNote() {

        CourseInfo mCourseInfo = dm.getCourse("android_async");
        String mText = "Sample test for create note";
        String mTitle = "Sample title for create note";

        int index = dm.createNewNote();
        NoteInfo mNotes = dm.getNotes().get(index);
        mNotes.setCourse(mCourseInfo);
        mNotes.setText(mText);
        mNotes.setTitle(mTitle);

        NoteInfo mTestNote = dm.getNotes().get(index);
        assertEquals(mTestNote.getText() , mText);
        assertEquals(mTestNote.getTitle() , mTitle);
        assertEquals(mTestNote.getCourse() , mCourseInfo);


    }

    @Test
    public void findSimilarNote() {

        CourseInfo mCourseInfo = dm.getCourse("android_async");
        String mText = "Sample test for create note";
        String mText2 = "Sample test for create note 2";
        String mTitle = "Sample title for create note";

        int index = dm.createNewNote();
        NoteInfo mNotes = dm.getNotes().get(index);
        mNotes.setCourse(mCourseInfo);
        mNotes.setText(mText);
        mNotes.setTitle(mTitle);

        int index2 = dm.createNewNote();
        NoteInfo mNotes2 = dm.getNotes().get(index2);
        mNotes2.setCourse(mCourseInfo);
        mNotes2.setText(mText2);
        mNotes2.setTitle(mTitle);

        int mTestIndex1 = dm.findNote(mNotes);
        assertEquals(mTestIndex1 , index);

        int mTestIndex2 = dm.findNote(mNotes2);
        assertEquals(mTestIndex2 , index2);

    }
}