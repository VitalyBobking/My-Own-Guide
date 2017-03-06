package diploma.edu.zp.guide_my_own.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.activity.CountryActivity;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.utils.CreateBitmapFromPath;

/**
 * Created by Val on 3/6/2017.
 */

public class DetailsFragment extends Fragment {
    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String SAVE_STATE_PLACE = "SAVE_STATE_PLACE";
    private Place mPlace;

    public static DetailsFragment newInstance(Place place) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ITEM, place);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mPlace = (Place) getArguments().get(EXTRA_ITEM);
        } else {
            mPlace = (Place) savedInstanceState.get(SAVE_STATE_PLACE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);

        ImageView ivPhoto = (ImageView) v.findViewById(R.id.ivPhoto);
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        TextView tvPlaceName = (TextView) v.findViewById(R.id.tvPlaceName);

        ivPhoto.setImageBitmap(CreateBitmapFromPath.loadImage(mPlace.getUrl_pic()));
        tvTitle.setText(mPlace.getTitle());
        tvPlaceName.setText(mPlace.getPlaceName());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((CountryActivity)getActivity()).getSupportActionBar();

        if (mPlace != null && actionBar != null){
            actionBar.setTitle(mPlace.getTitle());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_STATE_PLACE, mPlace);
    }
}
