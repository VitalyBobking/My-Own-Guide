package diploma.edu.zp.guide_my_own.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.camera2.Camera2BasicFragment;
import diploma.edu.zp.guide_my_own.fragment.CountryFragment;
import diploma.edu.zp.guide_my_own.fragment.DetailsFragment;




public class CountryActivity extends AppCompatActivity implements DetailsFragment.ElementsUpdated {
    private static final String SAVED = "SAVED";
    private boolean isWasEdited;

    public void setWasEdited(boolean wasEdited) {
        isWasEdited = wasEdited;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);

        String country = getIntent().getStringExtra(CountryFragment.EXTRA_COUNTRY);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(country);
        }
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.country_main, CountryFragment.newInstance(country), CountryFragment.class.getName());
            transaction.addToBackStack(CountryFragment.class.getName());
            transaction.commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        pressToHome();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pressToHome();
    }

    private void pressToHome () {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStackImmediate();
        } else {
            finishActivity();
            finish();
        }
    }
    private void finishActivity() {
        if(isWasEdited) {
            Intent intent = new Intent();
            intent.putExtra("name", true);
            setResult(7777, intent);
        }
    }

    @Override
    public void elementSelected() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CountryFragment.class.getName());
        if (fragment instanceof CountryFragment) {
            ((CountryFragment)fragment).updateRecyclerView();
        }
    }


}
