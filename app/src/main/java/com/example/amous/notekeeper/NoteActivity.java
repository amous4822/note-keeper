package com.example.amous.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.amous.notekeeper.Database.NotekeeperOpenHelper;

import java.util.List;

import static com.example.amous.notekeeper.Database.NoteKeeperDatabaseContract.*;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTE_ID = "com.example.amous.notekeeper.NOTE_ID";
    public static final int NO_ID_VALUE = -1;
    public static final String ORIGINAL_NOTE_ID = "com.example.amous.notekeeper.ORIGINAL_NOTE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.amous.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.amous.notekeeper.ORIGINAL_NOTE_TEXT";



    private NoteInfo mNotes = new NoteInfo(DataManager.getInstance().getCourses().get(0), "", "");
    private boolean mIsNewNote;
    private Spinner mSpinnerCourse;
    private EditText mNoteTitle;
    private EditText mNoteBody;
    private int newNote;
    private boolean cancelNote;
    private String originalNoteText;
    private String originalNoteTitle;
    private String originalCourseId;
    private int mNoteId;
    private NotekeeperOpenHelper mHelper;
    private int mCourseIdPos;
    private int mNoteTitlePos;
    private int mNoteTextPos;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHelper = new NotekeeperOpenHelper(this);

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
        } else {
            retainSavedStateBundle(savedInstanceState);
        }

        if (!mIsNewNote)
            loadNoteData();

    }

    private void loadNoteData() {

        SQLiteDatabase mDb = mHelper.getReadableDatabase();

        String selection = NoteInfoEntry._ID + " = ? ";
        String[] selectionArgs = {Integer.toString(mNoteId)};

        String[] notesColumns = {
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT};

        mCursor = mDb.query(NoteInfoEntry.TABLE_NAME, notesColumns,
                selection, selectionArgs, null, null, null);

        mCourseIdPos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTextPos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mCursor.moveToNext();

        readDisplayPassedIntent();
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

        if (mIsNewNote)
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
            } else {
                retainOriginalNoteValues();
            }
        } else {
            saveNote();
        }
    }

    @Override
    protected void onDestroy() {

        mHelper.close();
        super.onDestroy();
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
        mNoteId = intent.getIntExtra(NOTE_ID, NO_ID_VALUE);
        mIsNewNote = mNoteId == NO_ID_VALUE;
        if (mIsNewNote) {

            DataManager dm = DataManager.getInstance();
            newNote = dm.createNewNote();
            mNotes = dm.getNotes().get(newNote);

        } /*else {
            mNotes = DataManager.getInstance().getNotes().get(mNoteId);
        }*/

    }

    private void readDisplayPassedIntent() {

        String noteTitle = mCursor.getString(mNoteTitlePos);
        String courseId = mCursor.getString(mCourseIdPos);
        String noteText = mCursor.getString(mNoteTextPos);

        List<CourseInfo> course = DataManager.getInstance().getCourses();

        CourseInfo mCourse = DataManager.getInstance().getCourse(courseId);
        int pos = course.indexOf(mCourse);

        mSpinnerCourse.setSelection(pos);
        mNoteTitle.setText(noteTitle);
        mNoteBody.setText(noteText);

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
        } else if (id == R.id.action_next) {
            moveNext();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        int mlastIndex = DataManager.getInstance().getNotes().size() - 1;
        MenuItem mItem = menu.findItem(R.id.action_next);
        mItem.setEnabled(mNoteId < mlastIndex);

        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();

        mNoteId++;
        mNotes = DataManager.getInstance().getNotes().get(mNoteId);

        saveOriginalNoteValues();
        readDisplayPassedIntent();

        invalidateOptionsMenu();
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
