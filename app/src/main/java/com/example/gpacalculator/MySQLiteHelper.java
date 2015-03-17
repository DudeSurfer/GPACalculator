package com.example.gpacalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Here be Demons! CRUD methods so that Calculating GPA is a breeze!
 * I love ECP CEP 99
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Assignments";

    public MySQLiteHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE Assignments ( " +
                "Subject_Name TEXT_UNIQUE," +
                "Assignment_Name TEXT," +
                "Marks_Received NUMERIC," +
                "Total_Marks NUMERIC," +
                "Weightage NUMERIC" +
                ");";
        db.execSQL(CREATE_ASSIGNMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS Assignments");

        // create fresh table
        this.onCreate(db);
    }

    /* CRUD Operations */

    private static final String TABLE_ASSIGNMENTS = "Assignments";

    private static final String KEY_SUBJECT = "Subject_Name";
    private static final String KEY_NAME = "Assignment_Name";
    private static final String KEY_RECEIVED = "Marks_Received";
    private static final String KEY_MAX = "Total_Marks";
    private static final String KEY_WEIGHTAGE = "Weightage";

    private static final String[] COLUMNS = {KEY_SUBJECT, KEY_NAME, KEY_RECEIVED, KEY_MAX, KEY_WEIGHTAGE};

    public void addAssignment(Assignment assignment) {
        Log.d("addAssignment", assignment.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, assignment.getSubjName());
        values.put(KEY_NAME, assignment.getAssmName());
        values.put(KEY_RECEIVED, assignment.getScoreReceived());
        values.put(KEY_MAX, assignment.getScoreMax());
        values.put(KEY_WEIGHTAGE, assignment.getWeightage());

        db.insert(TABLE_ASSIGNMENTS,
                null,
                values);

        db.close();
    }

    public Assignment getAssignment(String subjName, String assmName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_ASSIGNMENTS,
                        COLUMNS,
                        " Subject_Name = ? AND Assignment_Name = ? ",
                        new String[] {subjName, assmName},
                        null,
                        null,
                        null,
                        null);

        if (cursor != null)
            cursor.moveToFirst();

        Assignment assignment = new Assignment();
        assignment.setSubjName(String.valueOf(cursor.getString(0)));
        assignment.setAssmName(String.valueOf(cursor.getString(1)));
        assignment.setScoreReceived(Float.parseFloat(cursor.getString(2)));
        assignment.setScoreMax(Float.parseFloat(cursor.getString(3)));
        assignment.setWeightage(Float.parseFloat(cursor.getString(4)));

        Log.d("getAssignment", assignment.toString());

        return assignment;

    }

    public List<Assignment> getAllAssignments() {
        List<Assignment> assignments = new LinkedList<>();

        String query = "SELECT * FROM " + TABLE_ASSIGNMENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Assignment assignment;
        if (cursor.moveToFirst()) {
            do {
                assignment = new Assignment();
                assignment.setSubjName(String.valueOf(cursor.getString(0)));
                assignment.setAssmName(String.valueOf(cursor.getString(1)));
                assignment.setScoreReceived(Float.parseFloat(cursor.getString(2)));
                assignment.setScoreMax(Float.parseFloat(cursor.getString(3)));
                assignment.setWeightage(Float.parseFloat(cursor.getString(4)));

                assignments.add(assignment);
            } while (cursor.moveToNext());
        }
        db.close();
        Log.d("getAllAssignments()", assignments.toString());

        return assignments;

    }

    public List<Assignment> getAssignmentsBySubject(String subjName) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Assignment> assignments = new LinkedList<>();

        Cursor cursor =
                db.query(TABLE_ASSIGNMENTS,
                        COLUMNS,
                        " Subject_Name = ?",
                        new String[] {subjName},
                        null,
                        null,
                        null,
                        null);

        Assignment assignment;
        if (cursor.moveToFirst()) {
            do {
                assignment = new Assignment();
                assignment.setSubjName(String.valueOf(cursor.getString(0)));
                assignment.setAssmName(String.valueOf(cursor.getString(1)));
                assignment.setScoreReceived(Float.parseFloat(cursor.getString(2)));
                assignment.setScoreMax(Float.parseFloat(cursor.getString(3)));
                assignment.setWeightage(Float.parseFloat(cursor.getString(4)));

                assignments.add(assignment);
            } while (cursor.moveToNext());
        }
        db.close();
        Log.d("getAssignmentsBySubject+("+subjName+")", assignments.toString());

        return assignments;

    }

    public List<String> getSubjectList() {
        List<Assignment> assignments = getAllAssignments();
        List<String> subjectList = new LinkedList<>();

        for (Assignment assignment : assignments) {
            String subjName = assignment.getSubjName();
            if (!subjectList.contains(subjName)) {
                subjectList.add(subjName);
            }
        }

        Log.d("getSubjectList", subjectList.toString());
        return subjectList;

    }

    public int updateAssignment(Assignment assignment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, assignment.getSubjName());
        values.put(KEY_NAME, assignment.getAssmName());
        values.put(KEY_RECEIVED, assignment.getScoreReceived());
        values.put(KEY_MAX, assignment.getScoreMax());
        values.put(KEY_WEIGHTAGE, assignment.getWeightage());

        int i = db.update(TABLE_ASSIGNMENTS,
                values,
                " Subject_Name = ? AND Assignment_Name = ? ",
                new String[] {String.valueOf(assignment.getSubjName()), String.valueOf(assignment.getAssmName()) });
        db.close();
        return i;
    }

    public void deleteAssignment(Assignment assignment) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ASSIGNMENTS,
                " Subject_Name = ? AND Assignment_Name = ? ",
                new String[] {String.valueOf(assignment.getSubjName()), String.valueOf(assignment.getAssmName()) });
        db.close();

        Log.d("deleteAssignment", assignment.toString());
    }


}
