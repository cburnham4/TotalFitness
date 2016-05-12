package letshangllc.allfitness.ActivitiesAndFragments.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);

        /* Retrieve values from caller intent */
        int exerciseID = getIntent().getIntExtra(getString(R.string.exercise_id), 0);
        String routineName = getIntent().getStringExtra(getString(R.string.intent_value_name));
        int typeId = getIntent().getIntExtra(getString(R.string.type_id), 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                adapter.addFragment(new AddLiftSetFragment(), "Add Set");
                adapter.addFragment(new PastLiftsFragment(), "Past Lifts");
                adapter.addFragment(new LiftGraphFragment(), "Graph");
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
