package letshangllc.allfitness.ActivitiesAndFragments.Activities;


import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import letshangllc.allfitness.AdsHelper;
import letshangllc.allfitness.EdgeToEdgeHelper;
import letshangllc.allfitness.R;
import letshangllc.allfitness.ActivitiesAndFragments.MainFragments.MuscleGroupFragment;
import letshangllc.allfitness.ActivitiesAndFragments.MainFragments.ExercisesFragment;
import letshangllc.allfitness.ActivitiesAndFragments.MainFragments.RoutinesFragment;

public class MainTabbedActivity extends AppCompatActivity {

    private AdsHelper adsHelper;
    private Timer adRefreshTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);
        EdgeToEdgeHelper.apply(this, findViewById(android.R.id.content));

        Toolbar toolbar;
        TabLayout tabLayout;
        ViewPager viewPager;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.vp_main_activity);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tl_main_activity);
        tabLayout.setupWithViewPager(viewPager);

        this.runAds();
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ExercisesFragment(), "Exercises");
        adapter.addFragment(new MuscleGroupFragment(), "Muscle Groups");
        adapter.addFragment(new RoutinesFragment(), "Routines");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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

    public void runAds(){
        adsHelper =  new AdsHelper(getWindow().getDecorView(), getResources().getString(R.string.admob_ad_main), this);

        adsHelper.setUpAds();
        int delay = 1000; // delay for 1 sec.
        int period = getResources().getInteger(R.integer.ad_refresh_rate);
        adRefreshTimer = new Timer();
        adRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                adsHelper.refreshAd();  // display the data
            }
        }, delay, period);
    }

    @Override
    protected void onPause() {
        if (adsHelper != null) {
            adsHelper.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adsHelper != null) {
            adsHelper.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adRefreshTimer != null) {
            adRefreshTimer.cancel();
            adRefreshTimer = null;
        }
        if (adsHelper != null) {
            adsHelper.onDestroy();
        }
        super.onDestroy();
    }
}
