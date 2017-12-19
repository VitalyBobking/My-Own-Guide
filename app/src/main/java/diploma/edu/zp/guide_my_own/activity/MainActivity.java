package diploma.edu.zp.guide_my_own.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.fragment.CreatePlaceFragment;
import diploma.edu.zp.guide_my_own.fragment.MapFragment;
import diploma.edu.zp.guide_my_own.fragment.PlacesFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CURRENT_FRAGMENT = "CURRENT_FRAGMENT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT);
            replaceContentView(fragment, fragment.getTag(), fragment.getTag());
        } else {
            replaceContentView(new MapFragment(), R.string.map);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            replaceContentView(new MapFragment(), R.string.map);
        } else if (id == R.id.nav_my_places) {
            replaceContentView(new PlacesFragment(), R.string.my_places);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceContentView(Fragment fragment, @StringRes int toolbarNameResId){
        String s = getResources().getString(toolbarNameResId);
        replaceContentView(fragment , s, s);
    }

    private void replaceContentView(Fragment fragment, String toolbarName, String tag){
        replaceFrameWithFragment(fragment, R.id.content_main, tag);
        getSupportActionBar().setTitle(toolbarName);
    }

    private void replaceFrameWithFragment(Fragment fragment, @IdRes int contentFrame, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment temp = getSupportFragmentManager().findFragmentByTag(tag);
        if (temp == null){
            temp = fragment;
        }
        transaction.replace(contentFrame, temp, tag);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_main);
        if (fragment != null)
            getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CreatePlaceFragment.CAMERA_RQ) {
                CreatePlaceFragment fragment = (CreatePlaceFragment)
                        getSupportFragmentManager().findFragmentByTag(CreatePlaceFragment.class.getName());
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
