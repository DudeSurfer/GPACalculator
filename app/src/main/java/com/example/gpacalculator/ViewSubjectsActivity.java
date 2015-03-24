package com.example.gpacalculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;


public class ViewSubjectsActivity extends ActionBarActivity {
    NavigableMap<Integer, Double> map;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    List<String> subjectList;
    List<Double> gpaList;
    Toolbar toolbar;
    TextView mGPA;
    ImageButton mAddSubject;
    Toast mToast;

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


        mRecyclerView = (RecyclerView)findViewById(R.id.subjectListView);
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
                new RecyclerItemSingleClickListener(this, new RecyclerItemSingleClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        String subjectName = subjectList.get(position);
                        Intent intent = new Intent(ViewSubjectsActivity.this, MainSubjectActivity.class);
                        intent.putExtra("SUBJECT_NAME", subjectName);
                        startActivity(intent);
                    }
                })
        );

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
                                    final int cardPosition = position;
                                    final String subjectName = subjectList.get(cardPosition);
                                    new AlertDialog.Builder(ViewSubjectsActivity.this)
                                            .setTitle("Delete Subject")
                                            .setMessage("Do you really want to delete "+subjectName+"?")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    List<Assignment> assignmentList= db.getAssignmentsBySubject(subjectName);
                                                    for (Assignment assignment : assignmentList) {
                                                        db.deleteAssignment(assignment);
                                                    }
                                                    finish();
                                                    startActivity(getIntent());
                                                }})
                                            .setNegativeButton(android.R.string.no, null).show();
                                }

                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    final int cardPosition = position;
                                    final String subjectName = subjectList.get(cardPosition);
                                    new AlertDialog.Builder(ViewSubjectsActivity.this)
                                            .setTitle("Delete Subject")
                                            .setMessage("Do you really want to delete "+subjectName+"?")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    List<Assignment> assignmentList= db.getAssignmentsBySubject(subjectName);
                                                    for (Assignment assignment : assignmentList) {
                                                        db.deleteAssignment(assignment);
                                                    }
                                                    finish();
                                                    startActivity(getIntent());
                                                }})
                                            .setNegativeButton(android.R.string.no, null).show();
                                }

                            }
                        });

        mRecyclerView.addOnItemTouchListener(swipeTouchListener);


        for (String subject : subjectList) {
            List<Assignment> subjectAssignmentList = db.getAssignmentsBySubject(subject);
            float totalPercentage = 0;
            float cWeightage = 0;
            double cGPA;

            for (Assignment assignment : subjectAssignmentList) {
                float weightage = assignment.getWeightage();
                float percentage = (assignment.getScoreReceived() / assignment.getScoreMax()) * weightage;
                totalPercentage += percentage;
                cWeightage += weightage;
            }

            float cPercentage = totalPercentage / cWeightage * 100; //get GPA percentage
            int perc = (int) Math.ceil(cPercentage); //round UP like what RI does
            cGPA = map.get(map.ceilingKey(perc)); //get the ceilingKEY

            gpaList.add(cGPA);
        }

        mGPA = (TextView) findViewById(R.id.cGPA);
        mGPA.setText("GPA: "+calculateAverage(gpaList));

        mAddSubject = (ImageButton) findViewById(R.id.addSubjectButton);
        mAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(ViewSubjectsActivity.this);
                View promptsView = li.inflate(R.layout.add_subject_prompt_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewSubjectsActivity.this);

                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.subjectNameText);

               alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("ADD",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String subjectName = userInput.getText().toString();
                                        if (db.getSubjectList().contains(subjectName)) {
                                            showToast("Subject with the name " + subjectName + " already exists.");

                                        }

                                        if (subjectName.isEmpty()) {
                                            showToast("Please enter a valid Subject Name");
                                        }

                                        else {
                                            Intent intent = new Intent(ViewSubjectsActivity.this, MainSubjectActivity.class);
                                            intent.putExtra("SUBJECT_NAME", subjectName);
                                            startActivity(intent);
                                        }

                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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
        if (num == 0) {
            return 0;
        }

        final double d = Math.ceil(Math.log10(num < 0 ? -num : num));
        final int power = n - (int) d;

        final double magnitude = Math.pow(10, power);
        final long shifted = Math.round(num * magnitude);
        return shifted / magnitude;
    }

    private void showToast(String textToShow) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, textToShow, Toast.LENGTH_SHORT);
        mToast.show();
    }




}
