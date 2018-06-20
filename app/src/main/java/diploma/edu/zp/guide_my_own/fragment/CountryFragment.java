package diploma.edu.zp.guide_my_own.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import diploma.edu.zp.guide_my_own.DBHelper.DBHelper;
import diploma.edu.zp.guide_my_own.DBHelper.DeletePlace;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.activity.CountryActivity;
import diploma.edu.zp.guide_my_own.adapter.CountryAdapter;
import diploma.edu.zp.guide_my_own.fragment.dialog.DialogToastFragment;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.utils.GetPlaces;
import rx.Subscriber;

/**
 * Created by Val on 2/24/2017.
 */

public class CountryFragment extends DialogToastFragment {
    public static final String EXTRA_COUNTRY = "EXTRA_COUNTRY";
    private String country;
    private RecyclerView recyclerView;
    private List<Place> places;
    private TextView empty_view;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_country, container, false);

        country = getArguments().getString(EXTRA_COUNTRY);
        recyclerView = v.findViewById(R.id.recycler_view);
        empty_view =  v.findViewById(R.id.empty_view);

        places = GetPlaces.getPlaces(getContext(), true, country);
        initViews(recyclerView, places);
        isEmptyPlace();


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Fragment fragment = getActivity().getSupportFragmentManager()
                .findFragmentByTag(CountryFragment.class.getName());
        if (fragment.isVisible()) {
            ActionBar actionBar = ((CountryActivity) getActivity()).getSupportActionBar();
            if (country != null && actionBar != null) {
                actionBar.setTitle(country);
            }

        }
    }

    private void initViews(RecyclerView recyclerView, List<Place> places){
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        adapter = new CountryAdapter(places);
        recyclerView.setAdapter(adapter);

        adapter.getViewClickedObservable().subscribe(new Subscriber<View>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("onError CountryFragment",e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(View view) {

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.country_main,
                        DetailsFragment.newInstance((Place) view.getTag()),
                        DetailsFragment.class.getName());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        adapter.getViewOnLongObservable().subscribe(new Subscriber<View>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                showErrorDialog(e.getMessage());
            }

            @Override
            public void onNext(View view) {
                Place p = (Place)view.getTag();
                int position = (p.getPosition());
                removeDialog(p.getId(), position);
            }
        });
    }

    private void removeDialog(int ids, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.you_sure))
                .setMessage(getString(R.string.you_lose_this_place))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                    boolean isSuccess = DeletePlace.delete(getContext(),ids);

                    if (isSuccess) {
                        ((CountryActivity)getActivity()).setWasEdited(true);

                        places.remove(position);
                        recyclerView.removeViewAt(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, places.size());
                        adapter.notifyDataSetChanged();

                        isEmptyPlace();
                        showSuccess(getString(R.string.deleted_success));
                    } else {
                        showErrorDialog(getString(R.string.something_went_wrong));
                    }
                }).setNegativeButton(android.R.string.no, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void updateRecyclerView() {
        if(places != null) {
            places = GetPlaces.getPlaces(getContext(), true, country);
            adapter.notifyDataSetChanged();
            isEmptyPlace();
        }

    }
    private void isEmptyPlace() {
        if (places.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);

        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }
    }

}
