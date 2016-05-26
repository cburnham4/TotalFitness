package letshangllc.allfitness.Database;

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
                    TableConstants.DayDateLifted + " text, " +
                    TableConstants.DayLiftComment + " text, " +
                    "FOREIGN KEY(" + TableConstants.ExerciseId + ") " +
                    "REFERENCES " + TableConstants.ExerciseTableName + "(" + TableConstants.ExerciseId + ") " +
                    " )";

    private static final String CREATE_SETS_TABLE =
            "CREATE TABLE " + TableConstants.SetsTableName+ " ( " +
                    TableConstants.SetsId+ " integer primary key AUTOINCREMENT, " +
                    TableConstants.DayId + " integer, " +
                    TableConstants.SetReps + " integer, " +
                    TableConstants.SetWeight + " real, " +
                    "FOREIGN KEY(" + TableConstants.DayId + ") " +
                    "REFERENCES " + TableConstants.DayTableName + "(" + TableConstants.DayId + ") " +
                    " )";

    private static final String CREATE_MAX_TABLE=
            "CREATE TABLE " + TableConstants.MaxTableName+ " ( " +
                    TableConstants.MaxId +" integer primary key AUTOINCREMENT, " +
                    TableConstants.DayId +" integer, " +
                    TableConstants.SetsId+ " integer, " +
                    TableConstants.ExerciseId + " integer, " +
                    TableConstants.MaxWeight +" REAL, " +
                    "FOREIGN KEY(" + TableConstants.DayId + ") " +
                    "REFERENCES " + TableConstants.DayTableName + "(" + TableConstants.DayId + ") " +
                    "FOREIGN KEY(" + TableConstants.ExerciseId + ") " +
                    "REFERENCES " + TableConstants.ExerciseTableName + "(" + TableConstants.ExerciseId + ") " +
                    "FOREIGN KEY(" + TableConstants.SetsId + ") " +
                    "REFERENCES " + TableConstants.SetsTableName + "(" + TableConstants.SetsId + ") " +
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
        db.execSQL(CREATE_SETS_TABLE);
        db.execSQL(CREATE_MAX_TABLE);
        //
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}