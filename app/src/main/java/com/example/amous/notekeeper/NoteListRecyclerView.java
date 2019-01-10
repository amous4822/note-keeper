package com.example.amous.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.amous.notekeeper.Database.NoteKeeperDatabaseContract;

public class NoteListRecyclerView extends RecyclerView.Adapter<NoteListRecyclerView.NoteListHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private Cursor mCursor;
    private int mNoteCoursePos;
    private int mNoteTitlePos;
    private int mNoteId;

    public NoteListRecyclerView(Context context, Cursor mNotes) {
        mContext = context;
        mCursor =mNotes;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPos();
    }

    private void populateColumnPos() {

        if(mCursor == null)
            return;

        mNoteCoursePos = mCursor.getColumnIndex(NoteKeeperDatabaseContract.CourseInfoEntry.COLUMN_COURSE_TITLE);
        mNoteTitlePos = mCursor.getColumnIndex(NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteId = mCursor.getColumnIndex(NoteKeeperDatabaseContract.NoteInfoEntry._ID);

    }

    public void changeCursor(Cursor cursor){

        if (mCursor != null)
            mCursor.close();

        mCursor=cursor;
        populateColumnPos();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mLayoutInflater.inflate(R.layout.list_note_item , viewGroup , false);
        return new NoteListHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListHolder noteListHolder, int i) {

        mCursor.moveToPosition(i);
        String noteCourse = mCursor.getString(mNoteCoursePos);
        String noteTitle = mCursor.getString(mNoteTitlePos);
        int noteId = mCursor.getInt(mNoteId);

        noteListHolder.mNoteTitle.setText(noteTitle);
        noteListHolder.mNoteCourse.setText(noteCourse);
        noteListHolder.mId = noteId;

    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class NoteListHolder extends RecyclerView.ViewHolder{

        public final TextView mNoteTitle;
        public final TextView mNoteCourse;
        private int mId;

        public NoteListHolder(@NonNull View itemView) {
            super(itemView);

            mNoteTitle = itemView.findViewById(R.id.note_title);
            mNoteCourse = itemView.findViewById(R.id.note_course);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(mContext , NoteActivity.class);
                    mIntent.putExtra(NoteActivity.NOTE_ID, mId);
                    mContext.startActivity(mIntent);
                }
            });
        }


    }
}
