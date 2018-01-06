package net.m3aak.parentapp.DatabasePackage;

/**
 * Created by BD-2 on 8/24/2015.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.m3aak.parentapp.Beans.Student;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "StudentManager";

    // Contacts table name
    private static final String TABLE_STUDENTS = "students";
//    {"child":[{"student_id":"14","s_fname":"Mion","s_lname":"Chi","s_email":"mion@mailinator.com","s_pass":"123456","s_address":"l;kjl","s_city":"21","s_state":null,"s_country":"2","s_contact":"234234","s_zip":"12312","s_image_path":"Mion816718235.png","s_parent_id":"4","s_school_id":"6"},{"student_id":"15","s_fname":"qn","s_lname":"Chi","s_email":"qn@mailinator.com","s_pass":"123456","s_address":"l;kjl","s_city":"21","s_state":null,"s_country":"2","s_contact":"234234","s_zip":"12312","s_image_path":"Mion816718235.png","s_parent_id":"4","s_school_id":"6"}],"result":"success"}

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_PH_NO = "s_contact";
    private static final String KEY_FIRST_NAME = "s_fname";
    private static final String KEY_LAST_NAME = "s_lname";
    private static final String KEY_EMAIL = "s_email";
    private static  final String KEY_ROUTE_ID="s_route_id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FIRST_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT," + KEY_STUDENT_ID + " TEXT," + KEY_LAST_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT" + "," + KEY_ROUTE_ID + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);

        // Create tables again
        onCreate(db);
    }


    // Getting All Contacts
    public List<Student> getAllStudents() {
        List<Student> contactList = new ArrayList<Student>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STUDENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setID(Integer.parseInt(cursor.getString(0)));
                student.setName(cursor.getString(1));
                student.setPhoneNumber(cursor.getString(5));
                student.setS_email(cursor.getString(2));
                student.setS_lname(cursor.getString(4));
                student.setStudent_id(cursor.getString(3));
                student.setS_route_id(cursor.getString(6));
                // Adding contact to list
                contactList.add(student);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }


}
