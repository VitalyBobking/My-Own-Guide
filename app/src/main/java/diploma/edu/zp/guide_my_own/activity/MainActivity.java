package diploma.edu.zp.guide_my_own.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.fragment.MapFragment;
import diploma.edu.zp.guide_my_own.fragment.PlacesFragment;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CURRENT_FRAGMENT = "CURRENT_FRAGMENT";
    private boolean fragmentPopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(() -> {
            if(getSupportFragmentManager().getBackStackEntryCount() == 0) finish();
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

           if(savedInstanceState == null) {
               MapFragment fragment = new MapFragment();
               FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
               ft.add(R.id.content_main, fragment,MapFragment.class.getName());
               ft.addToBackStack(MapFragment.class.getName());
               ft.setTransition(TRANSIT_FRAGMENT_FADE);
               ft.commit();
            }
        }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            MapFragment fragment = new MapFragment();
            String backStateName = fragment.getClass().getName();

            FragmentManager manager = getSupportFragmentManager();
            fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

            if (!fragmentPopped){
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.content_main, fragment,backStateName);
                ft.addToBackStack(backStateName);
                ft.commit();
            }
        } else if (id == R.id.nav_my_places) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            PlacesFragment fragment = new PlacesFragment();
            ft.replace(R.id.content_main, fragment);
            ft.addToBackStack(PlacesFragment.class.getName());
            ft.commit();
        }
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_main);
        if (fragment != null) {
            getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, fragment);
        }
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
             if(resultCode == 7777) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = fm.findFragmentById(R.id.content_main);
                if(fragment != null) {
                    if(fragment instanceof PlacesFragment){
                        ((PlacesFragment)fragment).updateAdapter();
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
