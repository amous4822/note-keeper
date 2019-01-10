package com.example.amous.notekeeper;


import android.annotation.SuppressLint;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.example.amous.notekeeper.Database.NotekeeperOpenHelper;

import static com.example.amous.notekeeper.Database.NoteKeeperDatabaseContract.*;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String NOTE_ID = "com.example.amous.notekeeper.NOTE_ID";
    public static final int NO_ID_VALUE = -1;
    private boolean mCourseStatus;
    private boolean mNotesStatus;
    public static final String ORIGINAL_NOTE_ID = "com.example.amous.notekeeper.ORIGINAL_NOTE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.amous.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.amous.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int LOADER_NOTES = 10;
    public static final int LOADER_COURSES = 11;


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
    private SimpleCursorAdapter mSpinnerAdapter;

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


        mSpinnerAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[]{android.R.id.text1}, 0);

        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourse.setAdapter(mSpinnerAdapter);

        getLoaderManager().initLoader(LOADER_COURSES,null , this);

        recieveIntents();
        if (savedInstanceState == null) {
            saveOriginalNoteValues();
        } else {
            retainSavedStateBundle(savedInstanceState);
        }

        if (!mIsNewNote)
            getLoaderManager().initLoader(LOADER_NOTES, null, this);

    }

    private void loadSpinnerData() {

        SQLiteDatabase mReadableDatabase = mHelper.getReadableDatabase();

        String[] columns = new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE,
                CourseInfoEntry.COLUMN_COURSE_ID,
                CourseInfoEntry._ID};

        Cursor mCursor = mReadableDatabase.query(CourseInfoEntry.TABLE_NAME, columns,
                null, null, null, null, CourseInfoEntry.COLUMN_COURSE_TITLE);
        mSpinnerAdapter.changeCursor(mCursor);

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
                deleteNoteFromDatabase();
            } else {
                retainOriginalNoteValues();
            }
        } else {
            saveNote();
        }
    }

    private void deleteNoteFromDatabase() {

        final String whereClause = NoteInfoEntry._ID + " = ?";
        final String[] whereArgs = {Integer.toString(mNoteId)};

        @SuppressLint("StaticFieldLeak") AsyncTask mTask = new AsyncTask() {
            @Nullable
            @Override
            protected Object doInBackground(Object[] objects) {

                SQLiteDatabase db = mHelper.getWritableDatabase();
                db.delete(NoteInfoEntry.TABLE_NAME, whereClause, whereArgs);
                return null;
            }
        };
        mTask.execute();

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

        String courseId= getSpinnerCourseId();
        String noteText = mNoteBody.getText().toString();
        String noteTitle = mNoteTitle.getText().toString();

        saveNotesToDatabase(courseId, noteTitle, noteText);

    }

    private String getSpinnerCourseId() {

        int coursePos = mSpinnerCourse.getSelectedItemPosition();
        Cursor mAdapterCursor = mSpinnerAdapter.getCursor();
        mAdapterCursor.moveToPosition(coursePos);
        int courseIndex = mAdapterCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);

        return mAdapterCursor.getString(courseIndex);
    }

    private void saveNotesToDatabase(String courseId, String noteTitle, String noteText){

        ContentValues mValues = new ContentValues();
        mValues.put(NoteInfoEntry.COLUMN_COURSE_ID , courseId);
        mValues.put(NoteInfoEntry.COLUMN_NOTE_TITLE , noteTitle);
        mValues.put(NoteInfoEntry.COLUMN_NOTE_TEXT , noteText);

        String whereClause = NoteInfoEntry._ID +  " = ?";
        String[] whereArgs = { Integer.toString(mNoteId)};

        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.update(NoteInfoEntry.TABLE_NAME , mValues, whereClause, whereArgs);
    }

    private void recieveIntents() {

        Intent intent = getIntent();
        mNoteId = intent.getIntExtra(NOTE_ID, NO_ID_VALUE);
        mIsNewNote = mNoteId == NO_ID_VALUE;
        if (mIsNewNote) {

            createNewNote();
        } /*else {
            mNotes = DataManager.getInstance().getNotes().get(mNoteId);
        }*/

    }

    private void createNewNote() {

        ContentValues mValues = new ContentValues();
        mValues.put(NoteInfoEntry.COLUMN_COURSE_ID, "");
        mValues.put(NoteInfoEntry.COLUMN_NOTE_TITLE, "");
        mValues.put(NoteInfoEntry.COLUMN_NOTE_TEXT, "");

        SQLiteDatabase db = mHelper.getWritableDatabase();
        mNoteId = (int) db.insert(NoteInfoEntry.TABLE_NAME, null, mValues);
    }

    private void readDisplayPassedIntent() {

        String noteTitle = mCursor.getString(mNoteTitlePos);
        String courseId = mCursor.getString(mCourseIdPos);
        String noteText = mCursor.getString(mNoteTextPos);

        int pos = getIndexCourseId(courseId);
        mSpinnerCourse.setSelection(pos);
        mNoteTitle.setText(noteTitle);
        mNoteBody.setText(noteText);

    }

    private int getIndexCourseId(String courseId) {

        Cursor mCursor = mSpinnerAdapter.getCursor();
        int courseIdPos = mCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        boolean mValue = mCursor.moveToFirst();
        int cursorPos = 0;

        while (mValue) {

            String cursorCourseId = mCursor.getString(courseIdPos);
            if (cursorCourseId.equals(courseId))
                break;

            cursorPos++;
            mValue = mCursor.moveToNext();

        }

        return cursorPos;
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader mLoader = null;
        if (LOADER_NOTES == id)
            mLoader = loadNotesCursor();

        else if (LOADER_COURSES == id)
            mLoader = loadCourseCursor();


        return mLoader;
    }

    @SuppressLint("StaticFieldLeak")
    private CursorLoader loadCourseCursor() {

        mCourseStatus = false;
        return new CursorLoader(this) {

            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase mReadableDatabase = mHelper.getReadableDatabase();

                String[] columns = new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE,
                        CourseInfoEntry.COLUMN_COURSE_ID,
                        CourseInfoEntry._ID};

                return mReadableDatabase.query(CourseInfoEntry.TABLE_NAME, columns,
                        null, null, null, null, CourseInfoEntry.COLUMN_COURSE_TITLE);
            }
        };
    }

    @SuppressLint("StaticFieldLeak")
    private CursorLoader loadNotesCursor() {

        mNotesStatus = false;
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase mDb = mHelper.getReadableDatabase();

                String selection = NoteInfoEntry._ID + " = ? ";
                String[] selectionArgs = {Integer.toString(mNoteId)};

                String[] notesColumns = {
                        NoteInfoEntry.COLUMN_COURSE_ID,
                        NoteInfoEntry.COLUMN_NOTE_TITLE,
                        NoteInfoEntry.COLUMN_NOTE_TEXT};

                return mDb.query(NoteInfoEntry.TABLE_NAME, notesColumns,
                        selection, selectionArgs, null, null, null);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loader.getId() == LOADER_NOTES)
            loadFinishedNotes(data);

        else if ( loader.getId() == LOADER_COURSES)
            loadFinishedCourse(data);


    }

    private void loadFinishedCourse(Cursor data) {

        mCourseStatus = true;
        mSpinnerAdapter.changeCursor(data);
        displayNotesWhenFinished();
    }

    private void loadFinishedNotes(Cursor data) {
        mCursor = data;
        mNotesStatus = true;

        mCourseIdPos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTextPos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        mNoteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mCursor.moveToNext();

        displayNotesWhenFinished();
    }

    private void displayNotesWhenFinished() {
        if (mNotesStatus && mCourseStatus)
            readDisplayPassedIntent();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (loader.getId() == LOADER_NOTES)
            if (mCursor != null)
                mCursor.close();

        else if (loader.getId() == LOADER_COURSES)
            mSpinnerAdapter.changeCursor(null);

    }
}
