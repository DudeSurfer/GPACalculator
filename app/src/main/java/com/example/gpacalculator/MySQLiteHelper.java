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

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Subject";


    public MySQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    //This Creates your Database to Read/Write
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SUBJECT_TABLE = "CREATE TABLE Subject(" +
                "Assignment_Name TEXT UNIQUE," +
                "Marks_Received NUMERIC," +
                "Total_Marks NUMERIC," +
                "Weightage NUMERIC" +
                ");";
        db.execSQL(CREATE_SUBJECT_TABLE);
        Log.d("onCreate()", "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS Subject");
        this.onCreate(db);
    }

    /* CRUD Operations */

    private static final String TABLE_SUBJECT = "Subject";

    private static final String KEY_NAME = "Assignment_Name";
    private static final String KEY_RECEIVED = "Marks_Received";
    private static final String KEY_MAX = "Total_Marks";
    private static final String KEY_WEIGHTAGE = "Weightage";

    private static final String[] COLUMNS = {KEY_NAME, KEY_RECEIVED, KEY_MAX, KEY_WEIGHTAGE};


    public void addAssignment(Assignment assignment){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, assignment.getAssmName());
        values.put(KEY_RECEIVED, assignment.getScoreReceived());
        values.put(KEY_MAX, assignment.getScoreMax());
        values.put(KEY_WEIGHTAGE, assignment.getWeightage());

        db.insert(TABLE_SUBJECT, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        db.close();

        Log.d("addAssignment()", "Assignment added successfully");
        Log.d("addAssignment()", values.toString());

    }

    public Assignment getAssignment(String assmName){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor =
                db.query(TABLE_SUBJECT,
                        COLUMNS,
                        " ASSIGNMENT_NAME = ?",
                        new String[] { String.valueOf(assmName)},
                        null,
                        null,
                        null,
                        null);
        if (cursor!=null){
            cursor.moveToFirst();
        }

        Assignment assignment = new Assignment();
        assignment.setAssmName(String.valueOf(cursor.getString(0)));
        assignment.setScoreReceived(Float.parseFloat(cursor.getString(1)));
        assignment.setScoreMax(Float.parseFloat(cursor.getString(2)));
        assignment.setWeightage(Float.parseFloat(cursor.getString(3)));

        Log.d("getAssignment("+assmName+")", assignment.toString());
        db.close();
        return assignment;
    }

    public List<Assignment> getAllAssignments() {
        List<Assignment> assignments = new LinkedList<>();

        String query = "SELECT * FROM " + TABLE_SUBJECT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Assignment assignment;
        if (cursor.moveToFirst()) {
            do {
                assignment = new Assignment();
                assignment.setAssmName(cursor.getString(0));
                assignment.setScoreReceived(Float.parseFloat(cursor.getString(1)));
                assignment.setScoreMax(Float.parseFloat(cursor.getString(2)));
                assignment.setWeightage(Float.parseFloat(cursor.getString(3)));

                assignments.add(assignment);
            } while (cursor.moveToNext());
        }
        db.close();
        Log.d("getAllAssignments()", assignments.toString());

        return assignments;

    }

    public int updateAssignment(Assignment assignment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, assignment.getAssmName());
        values.put(KEY_RECEIVED, assignment.getScoreReceived());
        values.put(KEY_MAX, assignment.getScoreMax());
        values.put(KEY_WEIGHTAGE, assignment.getWeightage());

        int i = db.update(TABLE_SUBJECT,
                values,
                KEY_NAME+" = ?",
                new String[] {String.valueOf(assignment.getAssmName())});
        db.close();
        return i;
    }

    public void deleteAssignment(Assignment assignment) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_SUBJECT,
                KEY_NAME+" = ?",
                new String[] {String.valueOf(assignment.getAssmName())});
        db.close();

        //Log.d("deleteAssignment", assignment.toString());
    }

}
