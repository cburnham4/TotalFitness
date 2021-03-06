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
import java.util.Timer;
import java.util.TimerTask;

import letshangllc.allfitness.ActivitiesAndFragments.typefragments.bodyweight.AddBodyWeightSetFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.bodyweight.GraphBodyWeightFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.bodyweight.PastBodyWeightFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio.AddCardioSetFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio.GraphCardioFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio.PastCardioFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.lift.AddLiftSetFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.lift.GraphLiftFragment;
import letshangllc.allfitness.ActivitiesAndFragments.typefragments.lift.PastLiftsFragment;
import letshangllc.allfitness.AdsHelper;
import letshangllc.allfitness.ClassObjects.bodyweight.BodyWeightSet;
import letshangllc.allfitness.ClassObjects.cardio.CardioSet;
import letshangllc.allfitness.ClassObjects.lift.LiftSet;
import letshangllc.allfitness.R;

public class ExerciseActivity extends AppCompatActivity implements AddLiftSetFragment.AddLiftSetListener,
        AddLiftSetFragment.DeleteLiftSetListner, AddLiftSetFragment.EditLiftSetListner,
        AddCardioSetFragment.AddCardioSetListener, AddCardioSetFragment.EditCardioSetListner,
        AddCardioSetFragment.DeleteCardioSetListner, AddBodyWeightSetFragment.AddBwSetListener,
        AddBodyWeightSetFragment.EditBwSetListner, AddBodyWeightSetFragment.DeleteBwSetListner
{
    private final static String TAG  = ExerciseActivity.class.getSimpleName();

    private ViewPagerAdapter adapter;

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

        this.runAds();
    }


    private void setupViewPager(ViewPager viewPager, int typeId) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        switch (typeId) {
            case 0: // BODYWEIGHT
                Fragment frag1Bw = new AddBodyWeightSetFragment();
                frag1Bw.setArguments(args);
                Fragment frag2Bw  = new PastBodyWeightFragment();
                frag2Bw.setArguments(args);
                Fragment frag3Bw = new GraphBodyWeightFragment();
                frag3Bw.setArguments(args);
                adapter.addFragment(frag1Bw , "Add Set");
                adapter.addFragment(frag2Bw , "HISTORY");
                adapter.addFragment(frag3Bw , "GRAPH");
                break;
            case 1: // CARDIO
                Fragment frag1Cardio  = new AddCardioSetFragment();
                frag1Cardio.setArguments(args);
                Fragment frag2Cardio  = new PastCardioFragment();
                frag2Cardio.setArguments(args);
                Fragment frag3Cardio = new GraphCardioFragment();
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
                Fragment frag3 = new GraphLiftFragment();
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
        PastLiftsFragment pastLiftsFragment = (PastLiftsFragment) adapter.getItem(1);

        /* Get update the graph if it has already been created */
        try{
            GraphLiftFragment graphLiftFragment = (GraphLiftFragment) adapter.getItem(2);
            graphLiftFragment.updateNewLiftSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastLiftsFragment.updateNewLiftSet(liftSet);
    }

    @Override
    public void deleteNewLiftSet(LiftSet liftSet) {
        PastLiftsFragment pastLiftsFragment = (PastLiftsFragment) adapter.getItem(1);

        /* Get update the graph if it has already been created */
        try{
            GraphLiftFragment graphLiftFragment = (GraphLiftFragment) adapter.getItem(2);
            graphLiftFragment.updateNewLiftSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Deleting Liftset");
        pastLiftsFragment.deleteLiftSet(liftSet);
    }

    @Override
    public void editNewLiftSet(LiftSet liftSet) {
        Log.e(TAG, "FRAGMENTS: "+ 0);
        PastLiftsFragment pastLiftsFragment = (PastLiftsFragment) adapter.getItem(1);

        /* Get update the graph if it has already been created */
        try{
            GraphLiftFragment graphLiftFragment = (GraphLiftFragment) adapter.getItem(2);
            graphLiftFragment.updateNewLiftSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastLiftsFragment.editLiftSet(liftSet);
    }

    @Override
    public void addCardioSet(CardioSet cardioSet) {
        PastCardioFragment pastCardioFragment = (PastCardioFragment) adapter.getItem(1);

                /* Get update the graph if it has already been created */
        try{
            GraphCardioFragment graphCardioFragment = (GraphCardioFragment) adapter.getItem(2);
            graphCardioFragment.updateCardioSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastCardioFragment.addCardioSet(cardioSet);
    }

    @Override
    public void deleteCardioSet(CardioSet cardioSet) {
        PastCardioFragment pastCardioFragment = (PastCardioFragment) adapter.getItem(1);

                /* Get update the graph if it has already been created */
        try{
            GraphCardioFragment graphCardioFragment = (GraphCardioFragment) adapter.getItem(2);
            graphCardioFragment.updateCardioSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastCardioFragment.deleteCardioSet(cardioSet);
    }

    @Override
    public void editCardioSet(CardioSet cardioSet) {
        PastCardioFragment pastCardioFragment = (PastCardioFragment) adapter.getItem(1);

                /* Get update the graph if it has already been created */
        try{
            GraphCardioFragment graphCardioFragment = (GraphCardioFragment) adapter.getItem(2);
            graphCardioFragment.updateCardioSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastCardioFragment.editCardioSet(cardioSet);
    }

    @Override
    public void addBwSet(BodyWeightSet bodyWeightSet) {
        PastBodyWeightFragment pastBodyWeightFragment = (PastBodyWeightFragment) adapter.getItem(1);
        /* Get update the graph if it has already been created */
        try{
            GraphBodyWeightFragment graphBodyWeightFragment = (GraphBodyWeightFragment) adapter.getItem(2);
            graphBodyWeightFragment.updateCardioSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastBodyWeightFragment.addBodyWeightSet(bodyWeightSet);
    }

    @Override
    public void deleteBwSet(BodyWeightSet bodyWeightSet) {
        PastBodyWeightFragment pastBodyWeightFragment = (PastBodyWeightFragment) adapter.getItem(1);
        /* Get update the graph if it has already been created */
        try{
            GraphBodyWeightFragment graphBodyWeightFragment = (GraphBodyWeightFragment) adapter.getItem(2);
            graphBodyWeightFragment.updateCardioSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastBodyWeightFragment.deleteBodyWeightSet(bodyWeightSet);
    }

    @Override
    public void editBwSet(BodyWeightSet bodyWeightSet) {
        PastBodyWeightFragment pastBodyWeightFragment = (PastBodyWeightFragment) adapter.getItem(1);
        /* Get update the graph if it has already been created */
        try{
            GraphBodyWeightFragment graphBodyWeightFragment = (GraphBodyWeightFragment) adapter.getItem(2);
            graphBodyWeightFragment.updateCardioSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pastBodyWeightFragment.editBodyWeightSet(bodyWeightSet);
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

    private AdsHelper adsHelper;
    public void runAds(){
        adsHelper =  new AdsHelper(getWindow().getDecorView(), getResources().getString(R.string.admob_ad_exercise), this);

        adsHelper.setUpAds();
        int delay = 1000; // delay for 1 sec.
        int period = getResources().getInteger(R.integer.ad_refresh_rate);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                adsHelper.refreshAd();  // display the data
            }
        }, delay, period);
    }
}
