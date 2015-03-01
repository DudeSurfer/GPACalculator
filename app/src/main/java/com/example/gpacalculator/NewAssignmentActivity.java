package com.example.gpacalculator;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;


public class NewAssignmentActivity extends ActionBarActivity {

    ImageButton mAddButton;
    Toast mToast;
    float cWeightage;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_assignment);
        final MySQLiteHelper db = new MySQLiteHelper(this); //get the database
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("Add a New Assignment");

        List<Assignment> assignmentList = db.getAllAssignments(); //List of Assignments
        for (Assignment assignment : assignmentList) {
            float weightage = assignment.getWeightage();
            cWeightage+=weightage; //get the total weightage of all assignments added SO FAR
        }

        mAddButton = (ImageButton) findViewById(R.id.add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Get them assignment Fields*/
                EditText assignmentNameField = (EditText) findViewById(R.id.assignment_name);
                EditText scoreReceivedField = (EditText) findViewById(R.id.score_received);
                EditText scoreMaxField = (EditText) findViewById(R.id.score_max);
                EditText weightageField = (EditText) findViewById(R.id.weightage);
                /*Store em*/
                String assignmentName = assignmentNameField.getText().toString().trim();
                String scoreReceived = scoreReceivedField.getText().toString().trim();
                String scoreMax = scoreMaxField.getText().toString().trim();
                String weightage = weightageField.getText().toString().trim();

                /*Check their validity*/
                if (assignmentName.isEmpty() || scoreReceived.isEmpty() || scoreMax.isEmpty() || weightage.isEmpty()) {
                    showToast("Please Complete All Fields");
                }

                else if (Float.parseFloat(scoreMax) == 0) {
                    showToast("Please enter a valid Maximum Score");
                }

                else if (Float.parseFloat(weightage) == 0) {
                    showToast("Please enter a valid Weightage");
                }

                else if (Float.parseFloat(scoreReceived)>Float.parseFloat(scoreMax)) {
                    showToast("Score Received cannot be greater than Maximum Possible Marks!");
                }

                else if((Float.parseFloat(weightage)+cWeightage)>100){
                    showToast("You cannot have more than 100% of credit per year!");
                }

                /*Pixie dust and Poof they're added to the database*/
                else {
                    db.addAssignment(new Assignment(assignmentName, Float.parseFloat(scoreReceived), Float.parseFloat(scoreMax), Float.parseFloat(weightage)));
                    finish();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_assignment, menu);
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


}
