package diploma.edu.zp.guide_my_own.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.camera2.Camera2BasicFragment;


public class Camera2Activity extends AppCompatActivity {

    public Camera2BasicFragment fragment;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera2);


       /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);*/

        if (savedInstanceState == null) {
            ft = getSupportFragmentManager().beginTransaction();
            fragment = new Camera2BasicFragment();
            ft.show(fragment);
            ft.replace(R.id.activity_camera2, fragment);
            ft.addToBackStack(Camera2BasicFragment.class.getName());
            ft.commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        pressToHome();
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void onBackPressed() {
       pressToHome();
    }


    private void pressToHome () {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStackImmediate();
            Fragment fragment = fm.findFragmentById(R.id.activity_camera2);
            if(fragment != null && fragment instanceof Camera2BasicFragment) {
                ((Camera2BasicFragment)fragment).invisibleButtons();
            }
        }  else {
            finish();
        }
    }

}
