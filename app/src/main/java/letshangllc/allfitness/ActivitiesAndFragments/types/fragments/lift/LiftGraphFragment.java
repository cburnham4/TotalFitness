package letshangllc.allfitness.ActivitiesAndFragments.types.fragments.lift;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiftGraphFragment extends Fragment {


    public LiftGraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lift_graph, container, false);
    }

}
