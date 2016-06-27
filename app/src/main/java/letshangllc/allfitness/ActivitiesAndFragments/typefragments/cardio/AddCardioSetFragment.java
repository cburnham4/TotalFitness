package letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import letshangllc.allfitness.ClassObjects.LiftSet;
import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.ListViewAdapters.SetListAdapter;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCardioSetFragment extends Fragment {
    /* Instance variables */
    private static final String TAG = AddCardioSetFragment.class.getSimpleName();
    /* Passed in lift variables */
    private int exerciseId;

    /* Current Date variable */
    String currentDate;

    /* Database Helper */
    DatabaseHelper databaseHelper;

    /* Day Id */
    int dayId;



    public AddCardioSetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        exerciseId = args.getInt(getString(R.string.exercise_id), 0);

        databaseHelper = new DatabaseHelper(this.getContext());

        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);
        Date date = new Date();
        currentDate = dateFormat.format(date);


        getExistingData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_cardio_set, container, false);

        //this.setupViews(view);


        return view;
    }

    /* find view and set their listeners */
    private void setupViews(View view){
        /* Find Views */
        Button addSet =(Button) view.findViewById(R.id.btn_addSet);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_clearValues);

    }

    public void getExistingData(){

    }



    /* Create context menu upon holding down listview row */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_context_edit_delete, menu);

    }

    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            // context menu logic
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch(item.getItemId()){
                case R.id.delete:

                    break;
                case R.id.edit:

                    break;

            }
            return true;
        }

        return false;
    }




}
