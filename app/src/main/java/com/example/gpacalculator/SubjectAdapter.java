package com.example.gpacalculator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by Vignesh Ravi on 16/3/2015.
 */
public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    NavigableMap<Integer, Double> map;
    private MySQLiteHelper db;
    private List<String> subjectList;

    public SubjectAdapter(MySQLiteHelper db, List<String> subjectList) {
        this.db = db;
        this.subjectList = subjectList;

        map = new TreeMap<>();
        map.put(0, 0.0);
        map.put(40, 0.8);
        map.put(49, 1.6);
        map.put(54, 2.0);
        map.put(59, 2.4);
        map.put(64, 2.8);
        map.put(69, 3.2);
        map.put(79, 3.6);
        map.put(100, 4.0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subject_card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String subjectName = subjectList.get(i);
        List<Assignment> subjectAssignmentList = db.getAssignmentsBySubject(subjectName);
        float tPercentage = 0;
        float cWeightage = 0;
        double cGPA;

        for (Assignment assignment : subjectAssignmentList) {
            float weightage = assignment.getWeightage();
            float percentage = (assignment.getScoreReceived() / assignment.getScoreMax()) * weightage;
            tPercentage += percentage;
            cWeightage += weightage;
        }

        float cPercentage = tPercentage / cWeightage * 100; //get GPA percentage
        int perc = (int) Math.ceil(cPercentage); //round UP like what RI does
        cGPA = map.get(map.ceilingKey(perc)); //get the ceilingKEY

        viewHolder.mSubjectName.setText(subjectName);
        viewHolder.mGPA.setText(String.valueOf(cGPA));
        viewHolder.mPercentageText.setText(String.valueOf(perc) + "%");

    }

    @Override
    public int getItemCount() {
        return subjectList == null ? 0 : subjectList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mSubjectName;
        public TextView mGPA;
        public TextView mPercentageText;

        public ViewHolder(View v) {
            super(v);
            mSubjectName = (TextView) v.findViewById(R.id.subject_name);
            mGPA = (TextView) v.findViewById(R.id.GPA);
            mPercentageText = (TextView) v.findViewById(R.id.percentage_text);
        }
    }

}
