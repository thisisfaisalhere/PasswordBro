package com.virusX.passwordBro;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "passwords";
    private static final String COL1 = "name";
    private static final String COL2 = "password";
    private static final String COL3 = "username";

    DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL1 + " VARCHAR2(30), " + COL2 + " VARCHAR(100000), " + COL3 + " VARCHAR2(30))";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(db);
    }

    boolean addData(String name, String pass, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, name);
        contentValues.put(COL2, pass);
        contentValues.put(COL3, username);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        Log.d(TAG, "DatabaseHelper: getData");
        return data;
    }

    void deleteData(int position){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE ID=" + position);
        Log.d(TAG, "DatabaseHelper: row deleted " + position);
    }

    void updateDate(int position, String name, String pass, String username){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME
                + " SET " + COL1 + "='" + name + "', "
                + COL2 + "='" + pass + "', " + COL3 + "='" + username + "' WHERE ID=" + position);
        Log.d(TAG, "DatabaseHelper: row update " + position);
    }


    boolean matchDataSet(ArrayList<String> nameList,
                         ArrayList<String> passwordList,
                         ArrayList<String> username) {
        int returnValue = -1;
        Cursor data = getData();
        if(data != null && data.getCount() > 0) {
            for(int i = 0; i < nameList.size(); i++) {
                String service = nameList.get(i);
                String key = passwordList.get(i);
                String user = username.get(i);
                SQLiteDatabase db = this.getWritableDatabase();
                String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 + "='" + service + "'" ;
                @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
                if(cursor == null) {
                    addData(service, key, user);
                    returnValue++;
                }
                data.moveToNext();
            }
        } else {
            for(int i = 0; i < nameList.size(); i++) {
                addData(nameList.get(i), passwordList.get(i), username.get(i));
                returnValue++;
            }
        }
        return returnValue != -1;
    }
}
