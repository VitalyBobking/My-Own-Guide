package diploma.edu.zp.guide_my_own.fragment;


import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.media.MediaCas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import diploma.edu.zp.guide_my_own.R;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FbFragment extends Fragment {

    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    public static final String USER_NAME = "user_posts";
    public LoginButton loginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fb, container, false);


        //LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
        callbackManager = CallbackManager.Factory.create();

        loginButton =  v.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL, USER_NAME));
        loginButton.setFragment(this);

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {

            }
        };
        accessTokenTracker.startTracking();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("result----->", String.valueOf(loginResult.getAccessToken().getUserId()));

                    Log.e("result----->", String.valueOf(loginResult.getAccessToken().getUserId()));
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "you have successfully registered",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();


            }
            @Override
            public void onCancel() {

               // getActivity().finish();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "You canceled",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }

            @Override
            public void onError(FacebookException exception) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        exception.getMessage(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        });
        return v;
}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
