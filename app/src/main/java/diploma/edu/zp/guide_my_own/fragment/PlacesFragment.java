package diploma.edu.zp.guide_my_own.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import diploma.edu.zp.guide_my_own.R;
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

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        TextView empty_view = (TextView) v.findViewById(R.id.empty_view);

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
        getActivity().getMenuInflater().inflate(R.menu.places_menu, menu);
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

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.places_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

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
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.content_main, CountryFragment.newInstance(String.valueOf(view.getTag())));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            //Remove swiped item
            adapter.remove(viewHolder.getAdapterPosition());
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            //Available drag and drop directions
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            //Available swipe directions
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        //Disable or Enable drag and drop by long press
        @Override
        public boolean isLongPressDragEnabled() {
            //return false;
            return true;
        }

        //Disable or Enable swiping
        @Override
        public boolean isItemViewSwipeEnabled() {
            //return false;
            return true;
        }

    };
}
