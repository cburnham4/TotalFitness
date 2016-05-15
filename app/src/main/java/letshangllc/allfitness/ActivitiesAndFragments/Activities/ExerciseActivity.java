package letshangllc.allfitness.ActivitiesAndFragments.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import letshangllc.allfitness.ActivitiesAndFragments.MainFragments.ExercisesFragment;
import letshangllc.allfitness.ActivitiesAndFragments.MainFragments.MuscleGroupFragment;
import letshangllc.allfitness.ActivitiesAndFragments.MainFragments.RoutinesFragment;
import letshangllc.allfitness.ActivitiesAndFragments.types.fragments.lift.AddLiftSetFragment;
import letshangllc.allfitness.ActivitiesAndFragments.types.fragments.lift.LiftGraphFragment;
import letshangllc.allfitness.ActivitiesAndFragments.types.fragments.lift.PastLiftsFragment;
import letshangllc.allfitness.R;

public class ExerciseActivity extends AppCompatActivity {
    private final static String TAG  = ExerciseActivity.class.getSimpleName();

    /* Bundle to pass to each of the fragments */
    Bundle args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);

        /* Retrieve values from caller intent */
        int exerciseId = getIntent().getIntExtra(getString(R.string.exercise_id), 0);
        String exerciseName = getIntent().getStringExtra(getString(R.string.intent_value_name));
        int typeId = getIntent().getIntExtra(getString(R.string.type_id), 5);

        args = new Bundle();
        args.putInt(getString(R.string.exercise_id), exerciseId);
        args.putInt(getString(R.string.type_id), typeId);
        args.putString(getString(R.string.intent_value_name), exerciseName);

        Log.i(TAG, "Type id = " + typeId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(exerciseName);

        if(toolbar != null){getSupportActionBar().setDisplayHomeAsUpEnabled(true);}
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_main_activity);
        setupViewPager(viewPager, typeId);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_main_activity);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager, int typeId) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        switch (typeId) {
            case 0: // BODYWEIGHT
                break;
            case 1: // CARDIO
                break;
            case 2: // LIFT
                Fragment frag1  = new AddLiftSetFragment();
                frag1.setArguments(args);
                Fragment frag2 = new PastLiftsFragment();
                frag2.setArguments(args);
                Fragment frag3 = new LiftGraphFragment();
                frag3.setArguments(args);
                adapter.addFragment(frag1, "Add Set");
                adapter.addFragment(frag2, "Past Lifts");
                adapter.addFragment(frag3, "Graph");
                break;
            case 3: // TIMED
                break;
        }

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
