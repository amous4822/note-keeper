package com.example.amous.notekeeper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteListRecyclerView extends RecyclerView.Adapter<NoteListRecyclerView.NoteListHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<NoteInfo> mNotes;

    public NoteListRecyclerView(Context context, List<NoteInfo> notes) {
        mContext = context;
        mNotes = notes;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public NoteListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mLayoutInflater.inflate(R.layout.list_note_item , viewGroup , false);
        return new NoteListHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListHolder noteListHolder, int i) {

        NoteInfo mNote = mNotes.get(i);
        noteListHolder.mNoteTitle.setText(mNote.getTitle());
        noteListHolder.mNoteText.setText(mNote.getCourse().getTitle());

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class NoteListHolder extends RecyclerView.ViewHolder{

        public TextView mNoteTitle;
        public TextView mNoteText;

        public NoteListHolder(@NonNull View itemView) {
            super(itemView);

            mNoteTitle = itemView.findViewById(R.id.note_title);
            mNoteText = itemView.findViewById(R.id.note_course);
        }
    }
}
