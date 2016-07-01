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

import letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio.AddCardioSetFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio.CardioGraphFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio.PastCardioFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.lift.AddLiftSetFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.lift.LiftGraphFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.lift.PastLiftsFragment;
import letshangllc.allfitness.ClassObjects.CardioSet;
import letshangllc.allfitness.ClassObjects.LiftSet;
import letshangllc.allfitness.R;

public class ExerciseActivity extends AppCompatActivity implements AddLiftSetFragment.AddLiftSetListener,
        AddLiftSetFragment.DeleteLiftSetListner, AddLiftSetFragment.EditLiftSetListner,
        AddCardioSetFragment.AddCardioSetListener, AddCardioSetFragment.EditCardioSetListner,
        AddCardioSetFragment.DeleteCardioSetListner
{
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
                Fragment frag1Cardio  = new AddCardioSetFragment();
                frag1Cardio.setArguments(args);
                Fragment frag2Cardio  = new PastCardioFragment();
                frag2Cardio.setArguments(args);
                Fragment frag3Cardio = new CardioGraphFragment();
                frag3Cardio.setArguments(args);
                adapter.addFragment(frag1Cardio, "Add Set");
                adapter.addFragment(frag2Cardio, "HISTORY");
                adapter.addFragment(frag3Cardio, "GRAPH");
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

    /* Lift Set Listeners */
    @Override
    public void addNewLiftSet(LiftSet liftSet) {
        PastLiftsFragment pastLiftsFragment = (PastLiftsFragment)getSupportFragmentManager().getFragments().get(0);

        /* Get update the graph if it has already been created */
        try{
            LiftGraphFragment liftGraphFragment = (LiftGraphFragment) getSupportFragmentManager().getFragments().get(2);
            liftGraphFragment.updateNewLiftSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastLiftsFragment.updateNewLiftSet(liftSet);
    }

    @Override
    public void deleteNewLiftSet(LiftSet liftSet) {
        PastLiftsFragment pastLiftsFragment = (PastLiftsFragment)getSupportFragmentManager().getFragments().get(0);

        /* Get update the graph if it has already been created */
        try{
            LiftGraphFragment liftGraphFragment = (LiftGraphFragment) getSupportFragmentManager().getFragments().get(2);
            liftGraphFragment.updateNewLiftSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Deleting Liftset");
        pastLiftsFragment.deleteLiftSet(liftSet);
    }

    @Override
    public void editNewLiftSet(LiftSet liftSet) {
        Log.e(TAG, "FRAGMENTS: "+ 0);
        PastLiftsFragment pastLiftsFragment = (PastLiftsFragment)getSupportFragmentManager().getFragments().get(0);

        /* Get update the graph if it has already been created */
        try{
            LiftGraphFragment liftGraphFragment = (LiftGraphFragment) getSupportFragmentManager().getFragments().get(2);
            liftGraphFragment.updateNewLiftSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastLiftsFragment.editLiftSet(liftSet);
    }

    @Override
    public void addCardioSet(CardioSet cardioSet) {
        PastCardioFragment pastCardioFragment = (PastCardioFragment) getSupportFragmentManager().getFragments().get(0);

                /* Get update the graph if it has already been created */
        try{
            CardioGraphFragment cardioGraphFragment = (CardioGraphFragment) getSupportFragmentManager().getFragments().get(2);
            cardioGraphFragment.updateCardioSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastCardioFragment.addCardioSet(cardioSet);
    }

    @Override
    public void deleteCardioSet(CardioSet cardioSet) {
        PastCardioFragment pastCardioFragment = (PastCardioFragment) getSupportFragmentManager().getFragments().get(0);

                /* Get update the graph if it has already been created */
        try{
            CardioGraphFragment cardioGraphFragment = (CardioGraphFragment) getSupportFragmentManager().getFragments().get(2);
            cardioGraphFragment.updateCardioSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastCardioFragment.deleteCardioSet(cardioSet);
    }

    @Override
    public void editCardioSet(CardioSet cardioSet) {
        PastCardioFragment pastCardioFragment = (PastCardioFragment) getSupportFragmentManager().getFragments().get(0);

                /* Get update the graph if it has already been created */
        try{
            CardioGraphFragment cardioGraphFragment = (CardioGraphFragment) getSupportFragmentManager().getFragments().get(2);
            cardioGraphFragment.updateCardioSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastCardioFragment.editCardioSet(cardioSet);
    }


    /* View Pager Adapter */
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
