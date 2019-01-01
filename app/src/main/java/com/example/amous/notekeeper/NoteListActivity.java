package com.example.amous.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private NoteListRecyclerView mListRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });

        initializeDisplayContent();

    }

    @Override
    protected void onResume() {

        super.onResume();
        mListRecyclerView.notifyDataSetChanged();

    }

    private void initializeDisplayContent() {

        final RecyclerView mList = findViewById(R.id.list_note);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mList.setLayoutManager(mLayoutManager);

        List<NoteInfo> mNotes = DataManager.getInstance().getNotes();
        mListRecyclerView = new NoteListRecyclerView(this, mNotes);
        mList.setAdapter(mListRecyclerView);

    }

}
