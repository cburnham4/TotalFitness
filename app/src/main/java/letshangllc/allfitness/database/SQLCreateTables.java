package letshangllc.allfitness.database;

/**
 * Created by cvburnha on 5/8/2016.
 */
public class SQLCreateTables {
    public static final String CREATE_EXERCISE_TABLE =
            "CREATE TABLE " + TableConstants.ExerciseTableName + " ( " +
                    TableConstants.ExerciseId + " integer primary key AUTOINCREMENT, " +
                    TableConstants.ExerciseName + " text, " +
                    TableConstants.ExerciseType + " integer, " +
                    TableConstants.MuscleID + " integer, " +
                    "FOREIGN KEY(" + TableConstants.MuscleID + ") " +
                    "REFERENCES " + TableConstants.MuscleTableName + "(" + TableConstants.MuscleID + ")" +
                    " )";

    public static final String CREATE_MUSCLE_TABLE =
            "CREATE TABLE " + TableConstants.MuscleTableName + " ( " +
                    TableConstants.MuscleID + " integer primary key AUTOINCREMENT, " +
                    TableConstants.MuscleName + " text " +
                    " )";

    public static final String CREATE_ROUTINE_TABLE =
            "CREATE TABLE " + TableConstants.RoutineTableName + " (" +
                    TableConstants.RoutineId + " integer primary key AUTOINCREMENT, " +
                    TableConstants.RoutineName + " text " +
                    ")";

    public static final String CREATE_ROUTINES_TABLE =
            "CREATE TABLE " + TableConstants.RoutinesTableName + " ( " +
                    TableConstants.RoutineId+ " integer, " +
                    TableConstants.ExerciseId + " integer, " +
                    "FOREIGN KEY(" + TableConstants.RoutineId + ") " +
                    "REFERENCES " + TableConstants.RoutineTableName + "(" + TableConstants.RoutineId + ") ," +
                    "FOREIGN KEY(" + TableConstants.ExerciseId + ") " +
                    "REFERENCES " + TableConstants.ExerciseTableName + "(" + TableConstants.ExerciseId + ") " +
                    " )";

    public static final String CREATE_DAYS_TABLE =
            "CREATE TABLE " + TableConstants.DayTableName + " ( " +
                    TableConstants.DayId+ " integer primary key AUTOINCREMENT, " +
                    TableConstants.ExerciseId + " integer, " +
                    TableConstants.DayDate + " text, " +
                    TableConstants.DayComment + " text, " +
                    "FOREIGN KEY(" + TableConstants.ExerciseId + ") " +
                    "REFERENCES " + TableConstants.ExerciseTableName + "(" + TableConstants.ExerciseId + ") " +
                    " )";

    public static final String CREATE_LIFT_SET_TABLE =
            "CREATE TABLE " + TableConstants.LiftSetsTableName + " ( " +
                    TableConstants.LiftSetsId + " integer primary key AUTOINCREMENT, " +
                    TableConstants.DayId + " integer, " +
                    TableConstants.LiftSetReps + " integer, " +
                    TableConstants.LiftSetWeight + " real, " +
                    "FOREIGN KEY(" + TableConstants.DayId + ") " +
                    "REFERENCES " + TableConstants.DayTableName + "(" + TableConstants.DayId + ") " +
                    " )";

    public static final String CREATE_MAX_TABLE =
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


    public static final String CREATE_CARDIO_SETS_TABLE =
            "CREATE TABLE " + TableConstants.CARDIO_SETS_TABLE_NAME + " ( " +
                    TableConstants.CARDIO_SETS_ID + " integer primary key AUTOINCREMENT, " +
                    TableConstants.CARDIO_SET_DISTANCE + " REAL, " +
                    TableConstants.CARDIO_SET_TIME + " REAL, " +
                    TableConstants.CARDIO_SET_HOURS + " INTEGER, " +
                    TableConstants.CARDIO_SET_MINUTES + " INTEGER, " +
                    TableConstants.CARDIO_SET_SECONDS + " INTEGER, " +
                    TableConstants.DayId + " integer, " +
                    "FOREIGN KEY(" + TableConstants.DayId + ") " +
                    "REFERENCES " + TableConstants.DayTableName + "(" + TableConstants.DayId + ") " +
                    " )";

    public static final String CREATE_BODY_WEIGHT_TABLE =
            "CREATE TABLE " + TableConstants.BODY_WEIGHT_TABLE_NAME + " ( " +
                    TableConstants.BODY_WEIGHT_SET_ID + " integer primary key AUTOINCREMENT, " +
                    TableConstants.BODY_WEIGHT_TIME + " REAL, " +
                    TableConstants.BODY_WEIGHT_REPS+ " INTEGER, " +
                    TableConstants.BODY_WEIGHT_MINUTES + " INTEGER, " +
                    TableConstants.BODY_WEIGHT_SECONDS + " INTEGER, " +
                    TableConstants.DayId + " integer, " +
                    "FOREIGN KEY(" + TableConstants.DayId + ") " +
                    "REFERENCES " + TableConstants.DayTableName + "(" + TableConstants.DayId + ") " +
                    " )";
}
