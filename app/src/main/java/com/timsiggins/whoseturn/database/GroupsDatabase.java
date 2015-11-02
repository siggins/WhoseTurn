package com.timsiggins.whoseturn.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    private static final String[] ALL_COLS = {COL_ID, COL_NAME, COL_LAST};
    private Context context;

    public static void onCreate(SQLiteDatabase db) {
       db.execSQL("CREATE TABLE " + TBLNAME + "(" +
                       COL_ID + " integer primary key autoincrement, " +
                       COL_NAME + " text not null, " +
                       COL_LAST + " datetime default current_timestamp);"
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
        g.setLastUsed(new Date(cursor.getLong(1)*1000));
        if (withPeople){
            g.addPeople(new PeopleDatabase(context).getPeopleForGroup(g.getId()));
        }
        return g;

    }
}
