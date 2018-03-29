package diploma.edu.zp.guide_my_own.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import diploma.edu.zp.guide_my_own.DBHelper.DeletePlace;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.activity.CountryActivity;
import diploma.edu.zp.guide_my_own.adapter.PlacesAdapter;
import diploma.edu.zp.guide_my_own.fragment.dialog.DialogToastFragment;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.utils.GetPlaces;
import rx.Subscriber;

/**
 * Created by Val on 2/17/2017.
 */

public class PlacesFragment extends DialogToastFragment {
    public PlacesFragment() {
        setHasOptionsMenu(true);
    }

    private PlacesAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_places, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        TextView empty_view = v.findViewById(R.id.empty_view);

        List<Place> places = GetPlaces.getPlaces(getContext(), true, null);

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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        //getActivity().getMenuInflater().inflate(R.menu.places_menu, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete) {
            Toast.makeText(getContext(), "SUPER", Toast.LENGTH_LONG).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(RecyclerView recyclerView, List<Place> places){
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));

        adapter = new PlacesAdapter(places);
        recyclerView.setAdapter(adapter);

        adapter.getViewClickedObservable().subscribe(new Subscriber<View>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                showErrorDialog(e.getMessage());
            }

            @Override
            public void onNext(View view) {
                Intent intent = new Intent(getActivity(), CountryActivity.class);
                intent.putExtra(CountryFragment.EXTRA_COUNTRY, String.valueOf(((Place)view.getTag()).getCountry()));
                startActivity(intent);
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
                removeCountry(p.getCountry());
            }
        });
    }

    private void removeCountry(String country) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.you_sure))
                .setMessage(getString(R.string.you_lose_this_place))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    List<Integer> ids = DeletePlace.deleteCountry(getContext(), country);
                    if (ids != null) {
                        adapter.remove(country);
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
}
