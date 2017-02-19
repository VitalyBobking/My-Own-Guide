package diploma.edu.zp.guide_my_own.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import diploma.edu.zp.guide_my_own.DBHelper.DBHelper;
import diploma.edu.zp.guide_my_own.DBHelper.PlaceScheme;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.adapter.PlacesAdapter;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.utils.GetPlaces;

/**
 * Created by Val on 2/17/2017.
 */

public class PlacesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_places, container, false);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        TextView empty_view = (TextView) v.findViewById(R.id.empty_view);

        List<Place> places = GetPlaces.getPlaces(getContext());

        initViews(recyclerView, places);

        if (places.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }

        return v;
    }

    private void initViews(RecyclerView recyclerView, List<Place> places){
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));

        PlacesAdapter adapter = new PlacesAdapter(places);
        recyclerView.setAdapter(adapter);
    }
}
