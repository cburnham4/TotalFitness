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

    private static final String CREATE_EXERCISE_TABLE =
            "CREATE TABLE " + TableConstants.ExerciseTableName + " ( " +
                    TableConstants.ExerciseId + " integer primary key AUTOINCREMENT, " +
                    TableConstants.ExerciseName + " text, " +
                    TableConstants.ExerciseType + " integer, " +
                    TableConstants.MuscleID + " integer, " +
                    "FOREIGN KEY(" + TableConstants.MuscleID + ") " +
                    "REFERENCES " + TableConstants.MuscleTableName + "(" + TableConstants.MuscleID + ")" +
                    " )";

    private static final String CREATE_MUSCLE_TABLE =
            "CREATE TABLE " + TableConstants.MuscleTableName + " ( " +
                    TableConstants.MuscleID + " integer primary key AUTOINCREMENT, " +
                    TableConstants.MuscleName + " text " +
                    " )";

    private static final String CREATE_ROUTINE_TABLE =
            "CREATE TABLE " + TableConstants.RoutineTableName + " (" +
                    TableConstants.RoutineId + " integer primary key AUTOINCREMENT, " +
                    TableConstants.RoutineName + " text " +
                    ")";

    private static final String CREATE_ROUTINES_TABLE =
            "CREATE TABLE " + TableConstants.RoutinesTableName + " ( " +
                    TableConstants.RoutineId+ " integer, " +
                    TableConstants.ExerciseId + " integer, " +
                    "FOREIGN KEY(" + TableConstants.RoutineId + ") " +
                    "REFERENCES " + TableConstants.RoutineTableName + "(" + TableConstants.RoutineId + ") ," +
                    "FOREIGN KEY(" + TableConstants.ExerciseId + ") " +
                    "REFERENCES " + TableConstants.ExerciseTableName + "(" + TableConstants.ExerciseId + ") " +
                    " )";

    private static final String CREATE_DAYS_TABLE =
            "CREATE TABLE " + TableConstants.DayTableName + " ( " +
                    TableConstants.DayId+ " integer primary key AUTOINCREMENT, " +
                    TableConstants.ExerciseId + " integer, " +
                    TableConstants.DayDate + " text, " +
                    TableConstants.DayComment + " text, " +
                    "FOREIGN KEY(" + TableConstants.ExerciseId + ") " +
                    "REFERENCES " + TableConstants.ExerciseTableName + "(" + TableConstants.ExerciseId + ") " +
                    " )";

    private static final String CREATE_LIFT_SET_TABLE =
            "CREATE TABLE " + TableConstants.LiftSetsTableName + " ( " +
                    TableConstants.LiftSetsId + " integer primary key AUTOINCREMENT, " +
                    TableConstants.DayId + " integer, " +
                    TableConstants.LiftSetReps + " integer, " +
                    TableConstants.LiftSetWeight + " real, " +
                    "FOREIGN KEY(" + TableConstants.DayId + ") " +
                    "REFERENCES " + TableConstants.DayTableName + "(" + TableConstants.DayId + ") " +
                    " )";

    private static final String CREATE_MAX_TABLE =
            "CREATE TABLE " + TableConstants.MaxTableName+ " ( " +
                    TableConstants.MaxId +" integer primary key AUTOINCREMENT, " +
                    TableConstants.DayId +" integer, " +
                    TableConstants.LiftSetsId + " integer, " +
                    TableConstants.ExerciseId + " integer, " +
                    TableConstants.MaxWeight +" REAL, " +
                    "FOREIGN KEY(" + TableConstants.DayId + ") " +
                    "REFERENCES " + TableConstants.DayTableName + "(" + TableConstants.DayId + ") " +
                    "FOREIGN KEY(" + TableConstants.ExerciseId + ") " +
                    "REFERENCES " + TableConstants.ExerciseTableName + "(" + TableConstants.ExerciseId + ") " +
                    "FOREIGN KEY(" + TableConstants.LiftSetsId + ") " +
                    "REFERENCES " + TableConstants.LiftSetsTableName + "(" + TableConstants.LiftSetsId + ") " +
                    " )";

    private static final String CREATE_CARDIO_SETS_TABLE =
            "CREATE TABLE " + TableConstants.CardioSetsTableName + " ( " +
                    TableConstants.CardioSetsId + " integer primary key AUTOINCREMENT, " +
                    TableConstants.CardioSetDistance + " REAL, " +
                    TableConstants.CardioSetTime + " REAL, " +
                    TableConstants.CardioSetHours + " INTEGER, " +
                    TableConstants.CardioSetMinutes + " INTEGER, " +
                    TableConstants.CardioSetSeconds + " INTEGER, " +
                    TableConstants.DayId + " integer, " +
                    "FOREIGN KEY(" + TableConstants.DayId + ") " +
                    "REFERENCES " + TableConstants.DayTableName + "(" + TableConstants.DayId + ") " +
                    " )";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        //if(checkDataBase()){
        db.execSQL(CREATE_EXERCISE_TABLE);
        db.execSQL(CREATE_MUSCLE_TABLE);
        db.execSQL(CREATE_ROUTINE_TABLE);
        db.execSQL(CREATE_ROUTINES_TABLE);
        db.execSQL(CREATE_DAYS_TABLE);
        db.execSQL(CREATE_LIFT_SET_TABLE);
        db.execSQL(CREATE_MAX_TABLE);
        db.execSQL(CREATE_CARDIO_SETS_TABLE);
        //
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}