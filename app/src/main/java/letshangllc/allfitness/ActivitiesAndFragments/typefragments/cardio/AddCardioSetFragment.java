package letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import letshangllc.allfitness.Database.DatabaseHelper;
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

    /* Views */
    private Button btnCancel, btnAddSet;
    private EditText etHour, etMinute, etSeconds, etMiles;



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

        this.findViews(view);
        this.setupViews();


        return view;
    }

    /* find view and set their listeners */
    private void findViews(View view){
        /* Find Views */
        btnAddSet =(Button) view.findViewById(R.id.btnAddCardioSet);
        btnCancel = (Button) view.findViewById(R.id.btnClearCardioValues);

        etHour = (EditText) view.findViewById(R.id.etHours);
        etMinute = (EditText) view.findViewById(R.id.etMiutes);
        etSeconds = (EditText) view.findViewById(R.id.etSeconds);
        etMiles = (EditText) view.findViewById(R.id.etMiles);

    }

    private void setupViews(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etHour.setText("");
                etMinute.setText("");
                etSeconds.setText("");
                etMiles.setText("");
            }
        });
        btnAddSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
