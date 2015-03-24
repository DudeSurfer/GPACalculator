package com.example.gpacalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;


public class EditAssignmentActivity extends ActionBarActivity {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    Toolbar toolbar;
    List<Assignment> assignmentList;
    Toast mToast;
    Intent intent;

    String SUBJECT_NAME;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assignment);
        final MySQLiteHelper db = new MySQLiteHelper(this);
        intent = getIntent();
        SUBJECT_NAME = intent.getStringExtra("SUBJECT_NAME");

       toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("View "+SUBJECT_NAME+" Assignments");

        assignmentList = db.getAssignmentsBySubject(SUBJECT_NAME);

        mRecyclerView = (RecyclerView)findViewById(R.id.assignments_list_view);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new AssignmentAdapter(assignmentList);
        mRecyclerView.setAdapter(mAdapter);

        if (assignmentList.size()>0) {
            showToast("Swipe to delete Assignment");
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (Assignment assignment : assignmentList) {
            double mPercentage = assignment.getScoreReceived() / assignment.getScoreMax() * 100;
            mPercentage = roundToSignificantFigures(mPercentage,3);
            float perc = (float) mPercentage;
            entries.add(new BarEntry(perc, assignmentList.indexOf(assignment)));
            labels.add(assignment.getAssmName());
        }

        BarChart chart = (BarChart) findViewById(R.id.assignments_histogram);

        /*Graph formatting*/
        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(getResources().getColor(R.color.primary_dark));
        BarData data = new BarData(labels, dataSet);
        chart.setData(data);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDescription("");

        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        leftAxis.setEnabled(false);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        chart.animateXY(1000, 1000);

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(mRecyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String assignmentName = assignmentList.get(position).getAssmName();
                                    Assignment deleteAssm = db.getAssignment(SUBJECT_NAME, assignmentName);
                                    db.deleteAssignment(deleteAssm);
                                    assignmentList.remove(position);

//                                    mAdapter = new AssignmentAdapter(db.getAssignmentsBySubject(SUBJECT_NAME));
//                                    mRecyclerView.setAdapter(mAdapter);
//                                    mAdapter.notifyDataSetChanged();

                                    showToast("You deleted "+assignmentName);

                                    finish();
                                    startActivity(getIntent());
                                }

                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String assignmentName = assignmentList.get(position).getAssmName();
                                    Assignment deleteAssm = db.getAssignment(SUBJECT_NAME, assignmentName);
                                    db.deleteAssignment(deleteAssm);
                                    assignmentList.remove(position);

//                                    mAdapter = new AssignmentAdapter(db.getAssignmentsBySubject(SUBJECT_NAME));
//                                    mRecyclerView.setAdapter(mAdapter);
//                                    mAdapter.notifyDataSetChanged();

                                    showToast("You deleted "+assignmentName);

                                    finish();
                                    startActivity(getIntent());
                                }

                            }
                        });

        mRecyclerView.addOnItemTouchListener(swipeTouchListener);




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_assignment, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String textToShow) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, textToShow, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static double roundToSignificantFigures(double num, int n) {
        if(num == 0) {
            return 0;
        }

        final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
        final int power = n - (int) d;

        final double magnitude = Math.pow(10, power);
        final long shifted = Math.round(num*magnitude);
        return shifted/magnitude;
    }

}
