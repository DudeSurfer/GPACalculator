package com.example.gpacalculator;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;


public class MainSubjectActivity extends ActionBarActivity {
    float tPercentage;
    float cPercentage;
    float cWeightage;
    double cGPA;
    NavigableMap<Integer,Double> map;
    ProgressBar mProgressBar;
    TextView mPercentage;
    TextView mGPA;
    ImageButton mNewAssignment;
    ImageButton mEditAssignment;
    Typeface typeface;
    MySQLiteHelper db;
    Toolbar toolbar;
    Intent intent;

    String SUBJECT_NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_main);
        typeface = Typeface.createFromAsset(getAssets(), "Roboto.ttf"); //setting typeface for lols

        intent = getIntent();
        SUBJECT_NAME = intent.getStringExtra("SUBJECT_NAME");

        db = new MySQLiteHelper(this); //define SQL Database

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(SUBJECT_NAME);

        /*This creates a NavigableTreeMap to get GPA more efficiently instead of writing ungodly amounts of code*/
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

        calcAndSet();

        mNewAssignment = (ImageButton) findViewById(R.id.add_assignment);
        mNewAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSubjectActivity.this, NewAssignmentActivity.class);
                intent.putExtra("SUBJECT_NAME", SUBJECT_NAME);
                startActivity(intent);
            }
        });

        mEditAssignment = (ImageButton) findViewById(R.id.edit_assignment);
        mEditAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSubjectActivity.this, EditAssignmentActivity.class);
                intent.putExtra("SUBJECT_NAME", SUBJECT_NAME);
                startActivity(intent);
            }
        });
    }

    @Override
    /*When coming back from 'ADD' screen, refresh and get the gpa again*/
    public void onResume(){
        super.onResume();
        calcAndSet();
    }

    public void calcAndSet() {

        List<Assignment> assignmentList = db.getAssignmentsBySubject(SUBJECT_NAME); //List of Assignments
        tPercentage = 0; //reset percentage and weightage
        cWeightage = 0;
        for (Assignment assignment : assignmentList) { //loop assignments
            float weightage = assignment.getWeightage();
            float percentage = (assignment.getScoreReceived()/assignment.getScoreMax())*weightage;
            tPercentage+=percentage;
            cWeightage+=weightage;
        }

        cPercentage = tPercentage/cWeightage*100; //get GPA percentage
        int perc = (int)Math.ceil(cPercentage); //round UP like what RI does
        cGPA = map.get(map.ceilingKey(perc)); //get the ceilingKEY

        /*Setting UI ELEMENTS here*/
        mProgressBar = (ProgressBar) findViewById(R.id.circularProgressbar);
        mGPA = (TextView) findViewById(R.id.cGPA);
        mPercentage = (TextView) findViewById(R.id.tPercentage);
        mGPA.setTypeface(typeface);
        mPercentage.setTypeface(typeface);
        int weightage = (int)Math.ceil(cWeightage);
        mProgressBar.setProgress(weightage);
        mPercentage.setText(String.valueOf(perc)+"%");
        mGPA.setText("GPA: "+String.valueOf(cGPA));
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
