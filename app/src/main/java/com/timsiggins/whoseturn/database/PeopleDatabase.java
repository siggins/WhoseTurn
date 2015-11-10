package com.timsiggins.whoseturn.database;

import android.content.ContentValues;
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
    private static final String COL_GRP = "group_id";
    private static final String COL_AHEAD = "ahead";

    private static final String[] ALL_COLS = {COL_ID, COL_GRP, COL_NAME, COL_LAST, COL_AHEAD};



    public static void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TBLNAME +" ("+
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
    public PeopleDatabase(DatabaseHelper dbHelper){
        this.dbHelper = dbHelper;
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
        p.setLastpaid(new Date(cursor.getLong(3)));
        return p;
    }


    public Person addPersonToGroup(int groupId, String inputText) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, inputText);
        contentValues.put(COL_GRP, groupId);
        contentValues.put(COL_LAST, new Date().getTime());
        contentValues.put(COL_AHEAD, 0);

        int id = (int) database.insert(TBLNAME,null,contentValues);
        return getPerson(id);
    }

    public Person getNextPayer(int groupId) {
        final List<Person> peopleForGroup = getPeopleForGroup(groupId);
        Person payer = peopleForGroup.get(0);
        for(Person p: peopleForGroup){
            if (p.getLastPaid().before(payer.getLastPaid())){
                payer = p;
            }
        }
        //todo -- take care of ahead
        return payer;
    }

    public void makePersonPay(int personId,  int groupId){
        Person p = getNextPayer(groupId);
        final ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LAST, new Date().getTime());
        if (p.getId() == personId){
            contentValues.put(COL_AHEAD,p.getAhead()+1);
        }
        database.update(TBLNAME,contentValues,COL_ID+" = ?",new String[]{personId + ""});
    }

    public Person getPerson(int id) {
        Person person = null;
        Cursor cursor = database.query(TBLNAME,ALL_COLS,COL_ID+"=?",new String[]{id+""},null,null,null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()){
            person = extractPerson(cursor);
        }
        cursor.close();
        return person;
    }


}
