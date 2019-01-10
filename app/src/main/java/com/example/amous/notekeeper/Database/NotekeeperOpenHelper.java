package com.example.amous.notekeeper.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotekeeperOpenHelper extends SQLiteOpenHelper {

    public static final String name = "Notekeeper.db";
    public static final int version = 2;

    //factory is used to change the way we interact with the database

    public NotekeeperOpenHelper(Context context) {

        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //creating tables
        db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(NoteKeeperDatabaseContract.NoteInfoEntry.SQL_CREATE_TABLE);

        //creating index
        db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.index1);
        db.execSQL(NoteKeeperDatabaseContract.NoteInfoEntry.index1);

        DatabaseDataWorker mWorker = new DatabaseDataWorker(db);
        mWorker.insertCourses();
        mWorker.insertSampleNotes();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2){

            //creating index for newer versions
            db.execSQL(NoteKeeperDatabaseContract.CourseInfoEntry.index1);
            db.execSQL(NoteKeeperDatabaseContract.NoteInfoEntry.index1);

        }

    }
}
