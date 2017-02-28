package diploma.edu.zp.guide_my_own.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.activity.MainActivity;
import diploma.edu.zp.guide_my_own.adapter.CountryAdapter;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.utils.GetPlaces;
import rx.Subscriber;

/**
 * Created by Val on 2/24/2017.
 */

public class CountryFragment extends Fragment {
    private static final String EXTRA_COUNTRY = "EXTRA_COUNTRY";

    public static CountryFragment newInstance(String country) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_COUNTRY, country);
        CountryFragment fragment = new CountryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public CountryFragment() {
        setHasOptionsMenu(true);
    }

    private CountryAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        ((MainActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.places_menu, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        /*((MainActivity)getActivity()).getToggle().setDrawerIndicatorEnabled(false);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);*/
    }

    @Override
    public void onStop() {
        super.onStop();
        //((MainActivity)getActivity()).getToggle().setDrawerIndicatorEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete) {
            Toast.makeText(getContext(), "SUPER", Toast.LENGTH_LONG).show();

            return true;
        } else if (id == android.R.id.home) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_places, container, false);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar2);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);

        String country = getArguments().getString(EXTRA_COUNTRY);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        TextView empty_view = (TextView) v.findViewById(R.id.empty_view);

        List<Place> places = GetPlaces.getPlaces(getContext(), true, country);

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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));

        adapter = new CountryAdapter(places);
        recyclerView.setAdapter(adapter);

        adapter.getViewClickedObservable().subscribe(new Subscriber<View>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(View view) {
                Log.e("view ---->", String.valueOf(((Place)view.getTag()).getPlaceName()));
            }
        });
    }
}
