package diploma.edu.zp.guide_my_own.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import diploma.edu.zp.guide_my_own.DBHelper.DBHelper;
import diploma.edu.zp.guide_my_own.DBHelper.PlaceScheme;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.adapter.PlacesAdapter;
import diploma.edu.zp.guide_my_own.model.Place;

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

        List<Place> places = getPlaces();

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

    private List<Place> getPlaces() {
        DBHelper dbHelper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Place> places = new ArrayList<>();

        try {
            dbHelper = new DBHelper(getContext());
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + DBHelper.FeedEntry.TABLE_NAME, null);

            if (cursor.moveToFirst()) {
                do {
                    Place place = new Place();
                    place.setId(cursor.getInt(PlaceScheme.ID.getIndex()));
                    place.setTitle(cursor.getString(PlaceScheme.TITLE.getIndex()));
                    place.setDescription(cursor.getString(PlaceScheme.DESCRIPTION.getIndex()));
                    place.setUrl_pic(cursor.getString(PlaceScheme.URL_PIC.getIndex()));
                    place.setLatitude(cursor.getDouble(PlaceScheme.LATITUDE.getIndex()));
                    place.setLongitude(cursor.getDouble(PlaceScheme.LONGITUDE.getIndex()));

                    places.add(place);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();

            if (db != null)
                db.close();

            if (dbHelper != null)
                dbHelper.close();
        }

        return places;
    }

    private void initViews(RecyclerView recyclerView, List<Place> places){
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        PlacesAdapter adapter = new PlacesAdapter(places);
        recyclerView.setAdapter(adapter);
    }
}
