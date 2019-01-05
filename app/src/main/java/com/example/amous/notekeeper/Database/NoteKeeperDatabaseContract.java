package com.example.amous.notekeeper.Database;

import android.provider.BaseColumns;

public final class NoteKeeperDatabaseContract {

    private NoteKeeperDatabaseContract(){}


    // new class for every table. table 1/2
    public static final class CourseInfoEntry implements BaseColumns {

        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";
        public static final String TABLE_NAME = "course_info";

        //create table statement

        public static final String SQL_CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME + "( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_COURSE_ID + " TEXT NOT NULL UNIQUE, " +
                COLUMN_COURSE_TITLE + " TEXT NOT NULL) ";

    }

    public static final class NoteInfoEntry implements BaseColumns{

        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String TABLE_NAME = "note_info";

        //create table statement

        public static final String SQL_CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME + "( " +
                _ID + " INTEGER PRIMARY KEY, " +
                COLUMN_COURSE_ID + " TEXT NOT NULL , " +
                COLUMN_NOTE_TEXT + " TEXT, " +
                COLUMN_NOTE_TITLE + " TEXT NOT NULL) ";

    }

}
