package com.timsiggins.whoseturn.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tim on 10/31/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "groups.db";
    private static final int DATABASE_VERSION = 5;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        GroupsDatabase.onCreate(db);
        PeopleDatabase.onCreate(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        GroupsDatabase.onUpgrade(db, i, i1);
        PeopleDatabase.onUpgrade(db, i, i1);
    }
}
