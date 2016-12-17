package upscaleapps.olympiad.TabBar;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import upscaleapps.olympiad.GoogleMaps.MapsFragment;
import upscaleapps.olympiad.Message.MessageFragment;
import upscaleapps.olympiad.Profile.ProfileFragment;
import upscaleapps.olympiad.R;
import upscaleapps.olympiad.Search.SearchFragment;


public class TabBarActivity extends AppCompatActivity{

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbar);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrags(new MessageFragment(), "Messages");
        viewPagerAdapter.addFrags(new SearchFragment(), "Search");
        viewPagerAdapter.addFrags(new MapsFragment(), "Maps");
        viewPagerAdapter.addFrags(new ProfileFragment(), "Profile");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
