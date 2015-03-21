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


public class AddSubjectActivity extends ActionBarActivity {

    Toolbar toolbar;
    ImageButton mAddButton;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        final MySQLiteHelper db = new MySQLiteHelper(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add Subject");


        mAddButton = (ImageButton) findViewById(R.id.add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText subjectNameField = (EditText) findViewById(R.id.subjectItemTextView);
                String subjectName = subjectNameField.getText().toString().trim();

                if (db.getSubjectList().contains(subjectName)){
                    showToast("Subject with the name "+subjectName+" already exists.");
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .playOn(subjectNameField);
                }

                if (subjectName.isEmpty()) {
                    showToast("Please enter a valid Subject Name");
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .playOn(subjectNameField);
                }

                else {
                    Intent intent = new Intent(AddSubjectActivity.this, MainSubjectActivity.class);
                    intent.putExtra("SUBJECT_NAME", subjectName);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_subject, menu);
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

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showToast(String textToShow) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, textToShow, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
