package com.timsiggins.whoseturn.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.timsiggins.whoseturn.data.Group;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tim on 10/31/15.
 */
public class GroupsDatabase {

    static final String TBLNAME = "groups";
    static final String COL_ID = "_id";
    private static final String COL_NAME = "name";
    private static final String COL_LAST = "last";
    private static final String COL_PIC = "picture";
    private static final String[] ALL_COLS = {COL_ID, COL_NAME, COL_LAST, COL_PIC};
    private Context context;

    public static void onCreate(SQLiteDatabase db) {
       db.execSQL("CREATE TABLE " + TBLNAME + " (" +
                       COL_ID + " integer primary key autoincrement, " +
                       COL_NAME + " text not null, " +
                       COL_LAST + " datetime default current_timestamp, " +
                       COL_PIC + " text);"
       );
    }

    public static void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TBLNAME);
        onCreate(db);
    }

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public GroupsDatabase(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }
    public void open(){
        database = dbHelper.getWritableDatabase();
    }
    public void close(){
        dbHelper.close();
    }
    public List<Group> getAllGroups(boolean withPeople){
        List<Group> groups = new ArrayList<>();
        Cursor cursor = database.query(TBLNAME,ALL_COLS,null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Group g = extractGroup(cursor, withPeople);
            groups.add(g);
            cursor.moveToNext();
        }
        cursor.close();
        return groups;
    }
    private Group extractGroup(Cursor cursor, boolean withPeople){
        Group g = new Group(cursor.getInt(0),cursor.getString(1));
        g.setLastUsed(new Date(cursor.getLong(2)));
        g.setPicture(cursor.getString(3));
        Log.d("GroupsDatabase", "setting pic to " + g.getPicture());
        if (withPeople){
            final PeopleDatabase peopleDatabase = new PeopleDatabase(dbHelper);
            peopleDatabase.open();
            g.addPeople(peopleDatabase.getPeopleForGroup(g.getId()));
            peopleDatabase.close();
        }
        return g;

    }
    public Group getGroup(long id, boolean withPeople){
        Group g = null;
        Cursor cursor = database.query(TBLNAME,ALL_COLS,COL_ID + " = ?",new String[]{id+""},null,null,null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()){
            g = extractGroup(cursor, withPeople);
        }
        cursor.close();
        return g;
    }

    public Group addGroup(String inputText) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, inputText);
        contentValues.put(COL_LAST, new Date().getTime());

        long id = database.insert(TBLNAME,null,contentValues);
        return getGroup(id,false);
    }

    public void addPicToGroup(int id, String filename) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PIC, filename);
        database.update(TBLNAME,contentValues,COL_ID+" = ?",new String[]{""+id});
    }
}
