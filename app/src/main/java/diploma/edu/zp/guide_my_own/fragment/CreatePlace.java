package diploma.edu.zp.guide_my_own.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import diploma.edu.zp.guide_my_own.R;

/**
 * Created by Val on 2/16/2017.
 */

public class CreatePlace extends Fragment {

    public static CreatePlace newInstance() {
        return new CreatePlace();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_place, container, false);



        return v;
    }
}
