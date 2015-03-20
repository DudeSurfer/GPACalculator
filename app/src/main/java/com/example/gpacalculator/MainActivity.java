package com.example.gpacalculator;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;


public class MainActivity extends ActionBarActivity {
    NavigableMap<Integer, Double> map;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    List<String> subjectList;
    List<Double> gpaList;
    Toolbar toolbar;
    TextView mGPA;
    ImageButton mAddSubject;

    MySQLiteHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        db = new MySQLiteHelper(this);

        /*Test CRUD
        db.addAssignment(new Assignment("Math","I love Math",5,20,10));
        db.addAssignment(new Assignment("Applied Math", "Meth",50,100,10));
        db.addAssignment(new Assignment("Sexy Math", "Mathemagic", 10, 30, 50));
        */


        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("GPA Calculator");


        mRecyclerView = (RecyclerView)findViewById(R.id.subjects_list_view);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        setRecyclerView();
    }

    public void onResume(){
        super.onResume();
        setRecyclerView();
    }

    public void setRecyclerView() {
        subjectList = db.getSubjectList();
        gpaList = new LinkedList<>();


        mAdapter = new SubjectAdapter(db, db.getSubjectList());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        String subjectName = subjectList.get(position);
                        Intent intent = new Intent(MainActivity.this, MainSubjectActivity.class);
                        intent.putExtra("SUBJECT_NAME", subjectName);
                        startActivity(intent);
                    }
                })
        );



        for (String subject : subjectList) {
            List<Assignment> subjectAssignmentList = db.getAssignmentsBySubject(subject);
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

            gpaList.add(cGPA);
        }

        mGPA = (TextView) findViewById(R.id.cGPA);
        mGPA.setText("GPA: "+calculateAverage(gpaList));

        mAddSubject = (ImageButton) findViewById(R.id.add_subject);
        final View mAddButton = findViewById(R.id.add_subject);
        mAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddSubjectActivity.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this, mAddButton, "Add");
                startActivity(intent, options.toBundle());
            }
        });


    }

    private double calculateAverage(List <Double> gpaList) {
        Double sum = 0.0;
        if(!gpaList.isEmpty()) {
            for (Double gpa : gpaList) {
                sum += gpa;
            }
            return roundToSignificantFigures(sum / gpaList.size(), 3);
        }
        return roundToSignificantFigures(sum,3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
