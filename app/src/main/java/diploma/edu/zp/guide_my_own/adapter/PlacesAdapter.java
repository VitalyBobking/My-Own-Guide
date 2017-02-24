package diploma.edu.zp.guide_my_own.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.model.Place;

/**
 * Created by Val on 2/17/2017.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {

    private List<Place> places;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_places_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Place place = places.get(position);
        holder.tvCategory.setText(place.getPlaceName());

        String path = place.getUrl_pic();

        if (path != null)
            holder.ivPicture.setImageBitmap(loadImage(path));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategory;
        private ImageView ivPicture;

        MyViewHolder(View view) {
            super(view);
            tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
        }
    }

    public void remove(int position) {
        places.remove(position);
        notifyItemRemoved(position);
    }

    public PlacesAdapter(List<Place> places) {
        this.places = places;
    }

    private Bitmap loadImage(String path) {
        Bitmap b = null;
        try {
            File f=new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }


}
