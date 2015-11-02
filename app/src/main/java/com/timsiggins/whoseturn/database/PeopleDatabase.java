package com.timsiggins.whoseturn.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.timsiggins.whoseturn.data.Group;
import com.timsiggins.whoseturn.data.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tim on 10/31/15.
 */
public class PeopleDatabase  {

    private static final String TBLNAME = "people";
    private static final String COL_ID = "_id";
    private static final String COL_NAME = "name";
    private static final String COL_LAST = "last";
    private static final String COL_GRP = "group";
    private static final String COL_AHEAD = "ahead";

    private static final String[] ALL_COLS = {COL_ID, COL_GRP, COL_NAME, COL_LAST, COL_AHEAD};



    public static void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TBLNAME +"("+
                        COL_ID+" integer primary key autoincrement, "+
                        COL_GRP+" integer not null, "+
                        COL_NAME+" text not null, "+
                        COL_LAST+" datetime default current_timestamp, "+
                        COL_AHEAD+" integer not null default 0, "+
                        "FOREIGN KEY ("+COL_GRP+") REFERENCES "+ GroupsDatabase.TBLNAME+" ("+GroupsDatabase.COL_ID+"));"
        );
    }

    public static void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TBLNAME);
        onCreate(db);
    }

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;


    public PeopleDatabase(Context context){
        dbHelper = new DatabaseHelper(context);
    }
    public void open(){
        database = dbHelper.getWritableDatabase();
    }
    public void close(){
        dbHelper.close();
    }

    public List<Person> getPeopleForGroup(int groupId){
        List<Person> groups = new ArrayList<>();
        Cursor cursor = database.query(TBLNAME,ALL_COLS,COL_GRP+"=?",new String[]{groupId+""},null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Person g = extractPerson(cursor);
            groups.add(g);
            cursor.moveToNext();
        }
        cursor.close();
        return groups;
    }

    private Person extractPerson(Cursor cursor) {
        Person p = new Person(cursor.getInt(0),cursor.getString(2));
        p.setAhead(cursor.getInt(4));
        p.setLastpaid(new Date(cursor.getLong(3)*1000));
        return null;
    }

    private void addPersonToGroup(Person p){

    }
}
