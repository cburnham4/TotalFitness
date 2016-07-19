package letshangllc.allfitness.ActivitiesAndFragments.typefragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;

/**
 * Created by Carl on 7/19/2016.
 */
public final class CommonFunctions {

    /* Add date to db id it does not already exist */
    public static final int addDateToDB(DatabaseHelper databaseHelper, String currentDate, int exerciseId, String TAG){
        /* First check if the db row has already been created */
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {TableConstants.DayId};

        /* Query the exercise table based on the muscle id to get all the associated exercises */
        Cursor c = db.query(TableConstants.DayTableName, projection, TableConstants.DayDate
                + " = '" + currentDate + "' AND " + TableConstants.ExerciseId +" = "+ exerciseId, null, null, null, null);

        c.moveToFirst();
        /* If there already exists a dayId for today then return it */
        if(!c.isAfterLast()){
            Log.e(TAG, "Day exists");
            int dayId = c.getInt(0);
            c.close();
            return dayId;
        }

        Log.e(TAG, "Day does not exist");

         /* Else insert in a new day */
        ContentValues values = new ContentValues();
        values.put(TableConstants.ExerciseId, exerciseId);
        values.put(TableConstants.DayDate, currentDate);

         /* Insert values into db */
        int dayId = (int) db.insert(TableConstants.DayTableName, null, values);
        db.close();

        /* Return the max Day id which will be the most recently inserted dayId */
        return dayId;

    }
}
