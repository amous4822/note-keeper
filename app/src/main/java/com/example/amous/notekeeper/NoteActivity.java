package com.example.amous.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_POSITION = "com.example.amous.notekeeper.NOTE_POSITION";
    public static final int NO_POSITION_VALUE = -1;
    public static final String ORIGINAL_NOTE_ID= "com.example.amous.notekeeper.ORIGINAL_NOTE_ID";
    public static final String ORIGINAL_NOTE_TITLE= "com.example.amous.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT= "com.example.amous.notekeeper.ORIGINAL_NOTE_TEXT";


    private NoteInfo mNotes;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourse;
    private EditText mNoteTitle;
    private EditText mNoteBody;
    private int newNote;
    private boolean cancelNote;
    private String originalNoteText;
    private String originalNoteTitle;
    private String originalCourseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerCourse = findViewById(R.id.spinner_editnote);
        mNoteTitle = findViewById(R.id.text_notetitle);
        mNoteBody = findViewById(R.id.text_notebody);

        List<CourseInfo> course = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> courseInfoArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, course);
        courseInfoArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourse.setAdapter(courseInfoArrayAdapter);

        recieveIntents();
        if (savedInstanceState == null) {
            saveOriginalNoteValues();
        }else {
            retainSavedStateBundle(savedInstanceState);
        }


        if (!mIsNewNote)
            readDisplayPassedIntent(mSpinnerCourse, mNoteTitle, mNoteBody);

    }

    private void retainSavedStateBundle(Bundle savedState) {

        originalCourseId = savedState.getString(ORIGINAL_NOTE_ID);
        originalNoteTitle = savedState.getString(ORIGINAL_NOTE_TITLE);
        originalNoteText = savedState.getString(ORIGINAL_NOTE_TEXT);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_ID, originalCourseId);
        outState.putString(ORIGINAL_NOTE_TEXT, originalNoteText);
        outState.putString(ORIGINAL_NOTE_TITLE, originalNoteTitle);
    }

    private void saveOriginalNoteValues() {

        if(mIsNewNote)
            return;

        originalCourseId = mNotes.getCourse().getCourseId();
        originalNoteTitle = mNotes.getTitle();
        originalNoteText = mNotes.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (cancelNote) {
            if (mIsNewNote) {
                DataManager.getInstance().removeNote(newNote);
            }else {
                retainOriginalNoteValues();
            }
        } else {
            saveNote();
        }
    }

    private void retainOriginalNoteValues() {

        CourseInfo course = DataManager.getInstance().getCourse(originalCourseId);
        mNotes.setCourse(course);
        mNotes.setTitle(originalNoteTitle);
        mNotes.setText(originalNoteText);

    }

    private void saveNote() {
        mNotes.setCourse((CourseInfo) mSpinnerCourse.getSelectedItem());
        mNotes.setText(mNoteBody.getText().toString());
        mNotes.setTitle(mNoteTitle.getText().toString());
    }

    private void recieveIntents() {

        Intent intent = getIntent();
        int position = intent.getIntExtra(NOTE_POSITION, NO_POSITION_VALUE);
        mIsNewNote = position == NO_POSITION_VALUE;
        if (mIsNewNote) {

            DataManager dm = DataManager.getInstance();
            newNote = dm.createNewNote();
            mNotes = dm.getNotes().get(newNote);

        } else {
            mNotes = DataManager.getInstance().getNotes().get(position);
        }

    }

    private void readDisplayPassedIntent(Spinner spinnerCourse, EditText noteTitle, EditText
            noteBody) {

        List<CourseInfo> course = DataManager.getInstance().getCourses();
        int pos = course.indexOf(mNotes.getCourse());
        spinnerCourse.setSelection(pos);

        noteTitle.setText(mNotes.getTitle());
        noteBody.setText(mNotes.getText());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            shareNotes();
            return true;
        } else if (id == R.id.cancel) {
            cancelNote = true;
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareNotes() {

        CourseInfo course = (CourseInfo) mSpinnerCourse.getSelectedItem();
        String subject = mNoteTitle.getText().toString();
        String body = "Checkout my notes on \"" + course.getTitle() + "\" \n\n" + mNoteBody.getText();

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, subject);
        share.putExtra(Intent.EXTRA_TEXT, body);

        startActivity(share);

    }
}
