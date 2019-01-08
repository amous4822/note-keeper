package com.example.amous.notekeeper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CoursesListRecyclerView extends RecyclerView.Adapter<CoursesListRecyclerView.CourseListHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<CourseInfo> mCourse;

    public CoursesListRecyclerView(Context context, List<CourseInfo> course) {
        mContext = context;
        mCourse = course;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CourseListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mLayoutInflater.inflate(R.layout.list_courses_item , viewGroup , false);
        return new CourseListHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseListHolder courseListHolder, int i) {

        CourseInfo mCourses = mCourse.get(i);
        courseListHolder.mCourseText.setText(mCourses.getTitle());
        courseListHolder.mCurrentPosition = i;

    }

    @Override
    public int getItemCount() {
        return mCourse.size();
    }

    public class CourseListHolder extends RecyclerView.ViewHolder{


        public final TextView mCourseText;
        private int mCurrentPosition;

        public CourseListHolder(@NonNull View itemView) {
            super(itemView);


            mCourseText = itemView.findViewById(R.id.note_courses);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Course card "+ mCurrentPosition +" is clicked", Toast.LENGTH_SHORT).show();
                    /*Intent mIntent = new Intent(mContext , NoteActivity.class);
                    mIntent.putExtra(NoteActivity.NOTE_ID , mCurrentPosition);
                    mContext.startActivity(mIntent);*/
                }
            });
        }


    }
}
