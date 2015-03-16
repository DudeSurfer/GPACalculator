package com.example.gpacalculator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by Vignesh Ravi on 1/3/2015.
 */
public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {
    NavigableMap<Integer,Double> map;
    private List<Assignment> assignmentList;
    DecimalFormat df;



    public AssignmentAdapter(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Assignment assignment = assignmentList.get(i);

        double mPercentage = assignment.getScoreReceived() / assignment.getScoreMax() * 100;

        map = new TreeMap<>();
        map.put(0,0.0);
        map.put(40,0.8);
        map.put(49,1.6);
        map.put(54,2.0);
        map.put(59,2.4);
        map.put(64,2.8);
        map.put(69,3.2);
        map.put(79,3.6);
        map.put(100,4.0);

        df = new DecimalFormat("0.#");

        int perc = (int)Math.ceil(mPercentage);

        String assignmentName = assignment.getAssmName();
        if (assignmentName.length() > 18) {
            assignmentName = assignmentName.substring(0, Math.min(assignmentName.length(), 15));
            assignmentName+="...";
        }

        viewHolder.mAssignmentName.setText(assignmentName);
        viewHolder.mGPA.setText(String.valueOf(map.get(map.ceilingKey(perc))));
        viewHolder.mPercentageText.setText(String.valueOf(perc)+"%");
        viewHolder.mScoreText.setText(String.valueOf(df.format(assignment.getScoreReceived()))+"/"+String.valueOf(df.format(assignment.getScoreMax())));

        map.clear();
    }

    @Override
    public int getItemCount() {
        return assignmentList == null ? 0 : assignmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mAssignmentName;
        public TextView mGPA;
        public TextView mScoreText;
        public TextView mPercentageText;

        public ViewHolder(View v) {
            super(v);
            mAssignmentName = (TextView) v.findViewById(R.id.assignment_name);
            mGPA = (TextView) v.findViewById(R.id.GPA);
            mScoreText = (TextView) v.findViewById(R.id.score_text);
            mPercentageText = (TextView) v.findViewById(R.id.percentage_text);
        }
    }
}
