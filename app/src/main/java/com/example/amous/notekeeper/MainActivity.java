package com.example.amous.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.amous.notekeeper.Database.NotekeeperOpenHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NoteListRecyclerView mNoteRecyclerView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private CoursesListRecyclerView mCoursesRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private NotekeeperOpenHelper mHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHelper = new NotekeeperOpenHelper(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });




        PreferenceManager.setDefaultValues(this, R.xml.pref_general,false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification,false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync,false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // connects nav_view and this class to implement listener.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNoteRecyclerView.notifyDataSetChanged();
        updateNavHeader();
    }

    @Override
    protected void onDestroy() {
        mHelper.close();
        super.onDestroy();

    }

    private void updateNavHeader() {

        NavigationView mNavView = findViewById(R.id.nav_view);
        View mHeader = mNavView.getHeaderView(0);

        TextView mUserName = mHeader.findViewById(R.id.text_user_name);
        TextView mUserMail = mHeader.findViewById(R.id.text_user_mail);

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String name = mPref.getString("pref_general_name" , "");
        String mail = mPref.getString("pref_general_email" , "");

        mUserMail.setText(mail);
        mUserName.setText(name);

    }

    private void initializeDisplayContent() {


        DataManager.loadFromDatabase(mHelper);
        mRecyclerView = findViewById(R.id.list_item);

        mLinearLayoutManager = new LinearLayoutManager(this);
        List<NoteInfo> mNotes = DataManager.getInstance().getNotes();
        mNoteRecyclerView = new NoteListRecyclerView(this, mNotes);

        mGridLayoutManager = new GridLayoutManager(this , getResources().getInteger(R.integer.course_grid_number));
        List<CourseInfo> mCourses = DataManager.getInstance().getCourses();
        mCoursesRecyclerView = new CoursesListRecyclerView(this , mCourses);

        displayNote();

    }

    private void displayNote() {
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mNoteRecyclerView);
        navSelectionId(R.id.nav_notes);

    }

    private void navSelectionId(int id) {
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        Menu mMenu = mNavigationView.getMenu();
        mMenu.findItem(id).setChecked(true);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    //since this class implements a nav_listener some function should be there to detect the selected item hence this VVV.
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            displayNote();

        } else if (id == R.id.nav_courses) {
            displayCourses();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayCourses() {

        mRecyclerView.setAdapter(mCoursesRecyclerView);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        navSelectionId(R.id.nav_courses);

    }
}
