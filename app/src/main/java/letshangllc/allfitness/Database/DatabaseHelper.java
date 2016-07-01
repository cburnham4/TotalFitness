package letshangllc.allfitness.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cvburnha on 5/8/2016.
 */
public class DatabaseHelper  extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LiftsDatabase.db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        //if(checkDataBase()){
        db.execSQL(SQLCreateTables.CREATE_EXERCISE_TABLE);
        db.execSQL(SQLCreateTables.CREATE_MUSCLE_TABLE);
        db.execSQL(SQLCreateTables.CREATE_ROUTINE_TABLE);
        db.execSQL(SQLCreateTables.CREATE_ROUTINES_TABLE);
        db.execSQL(SQLCreateTables.CREATE_DAYS_TABLE);
        db.execSQL(SQLCreateTables.CREATE_LIFT_SET_TABLE);
        db.execSQL(SQLCreateTables.CREATE_MAX_TABLE);
        db.execSQL(SQLCreateTables.CREATE_CARDIO_SETS_TABLE);
        db.execSQL(SQLCreateTables.CREATE_BODY_WEIGHT_TABLE);
        //
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}