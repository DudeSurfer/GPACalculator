package com.example.gpacalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Arrays;
import java.util.List;


public class AddAssignmentActivity extends ActionBarActivity {

    ImageButton mAddButton;
    Toast mToast;
    float cWeightage;
    Toolbar toolbar;
    Intent intent;

    String SUBJECT_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_assignment);

        final MySQLiteHelper db = new MySQLiteHelper(this); //get the database
        intent = getIntent();
        SUBJECT_NAME = intent.getStringExtra("SUBJECT_NAME");

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add "+SUBJECT_NAME+" Assignment");

        List<Assignment> assignmentList = db.getAssignmentsBySubject(SUBJECT_NAME); //List of Assignments
        for (Assignment assignment : assignmentList) {
            float weightage = assignment.getWeightage();
            cWeightage+=weightage; //get the total weightage of all assignments added SO FAR
        }

        mAddButton = (ImageButton) findViewById(R.id.add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Get them assignment Fields*/
                EditText assignmentNameField = (EditText) findViewById(R.id.assignmentEditText);
                EditText scoreReceivedField = (EditText) findViewById(R.id.scoreEditText);
                EditText scoreMaxField = (EditText) findViewById(R.id.maxScoreEditText);
                EditText weightageField = (EditText) findViewById(R.id.weightageEditText);

                EditText[] FIELDS = {assignmentNameField, scoreReceivedField, scoreMaxField, weightageField};

                /*Store em*/
                String assignmentName = assignmentNameField.getText().toString().trim();
                String scoreReceived = scoreReceivedField.getText().toString().trim();
                String scoreMax = scoreMaxField.getText().toString().trim();
                String weightage = weightageField.getText().toString().trim();

                String[] INPUT = {assignmentName,scoreReceived,scoreMax,weightage};
                List<String> INPUT_ARRAY = Arrays.asList(INPUT);

                /*Check their validity*/
                try {
                    Float.parseFloat(scoreReceived);
                } catch (Exception e) {
                    showToast("Please ensure that your Received Score is valid");
                    wobble(scoreReceivedField, 500);
                    return;
                }

                try {
                    Float.parseFloat(scoreMax);
                } catch (Exception e) {
                    showToast("Please ensure that your Maximum Score is valid");
                    wobble(scoreMaxField, 500);
                    return;
                }

                try {
                    Float.parseFloat(weightage);
                } catch (Exception e) {
                    showToast("Please ensure that your Weightage is valid");
                    wobble(weightageField, 500);
                    return;
                }

                for (String text : INPUT){
                    if (text.isEmpty()){
                        EditText textField = FIELDS[INPUT_ARRAY.indexOf(text)];
                        wobble(textField, 500);
                    }
                }

                if (assignmentName.isEmpty() || scoreReceived.isEmpty() || scoreMax.isEmpty() || weightage.isEmpty()) {
                    showToast("Please Complete All Fields");
                }

                else if (Float.parseFloat(scoreMax) == 0 ) {
                    showToast("Please enter a valid Maximum Score");
                    wobble(scoreMaxField, 500);

                }

                else if (Float.parseFloat(weightage) == 0) {
                    showToast("Please enter a valid Weightage");
                    wobble(weightageField, 500);
                }

                else if (Float.parseFloat(scoreReceived)>Float.parseFloat(scoreMax)) {
                    showToast("Score Received cannot be greater than Maximum Possible Marks!");
                    wobble(scoreReceivedField, 500);
                    wobble(scoreMaxField, 500);
                }

                else if((Float.parseFloat(weightage)+cWeightage)>100){
                    showToast("You cannot have more than 100% of credit per year!");
                    wobble(weightageField, 500);
                }

                /*Pixie dust and Poof they're added to the database*/
                else {
                    db.addAssignment(new Assignment(SUBJECT_NAME, assignmentName, Float.parseFloat(scoreReceived), Float.parseFloat(scoreMax), Float.parseFloat(weightage)));
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

    private void wobble(EditText field, int duration) {
        YoYo.with(Techniques.Wobble)
                .duration(duration)
                .playOn(field);
    }


}
