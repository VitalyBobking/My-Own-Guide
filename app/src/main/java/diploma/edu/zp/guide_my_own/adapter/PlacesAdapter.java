package diploma.edu.zp.guide_my_own.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jakewharton.rxbinding.view.RxView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;
import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.model.Place;
import rx.Observable;
import rx.subjects.PublishSubject;



public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {
    private PublishSubject<View> mViewClickSubject = PublishSubject.create();
    private PublishSubject<View> mViewOnLongClickSubject = PublishSubject.create();
    public Observable<View> getViewClickedObservable() {
        return mViewClickSubject.asObservable();
    }
    public Observable<View> getViewOnLongObservable() {
        return mViewOnLongClickSubject.asObservable();
    }
    private List<Place> places;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_places_list, parent, false);

        RxView.clicks(itemView)
                .takeUntil(RxView.detaches(parent))
                .map(aVoid -> itemView)
                .subscribe(mViewClickSubject);

        RxView.longClicks(itemView)
                .takeUntil(RxView.detaches(parent))
                .map(aVoid -> itemView)
                .subscribe(mViewOnLongClickSubject);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Place place = places.get(position);
        holder.tvCategory.setText(place.getCountry());

        place.setPosition(position);

        holder.itemView.setTag(place);

        String path = place.getUrl_pic();

        if (path != null) {
            ImageLoader.getInstance().displayImage("file:///"+path, holder.ivPicture);
        }
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
            tvCategory = view.findViewById(R.id.tvCategory);
            ivPicture = view.findViewById(R.id.ivPicture);
        }
    }

    public void remove(int position) {
        places.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(String country) {
        int position = 0;
        for (Place p : places) {
            if (p.getCountry().equals(country)) {
                places.remove(position);
                notifyItemRemoved(position);
            }
            position++;
        }
    }

    public PlacesAdapter(List<Place> places) {
        this.places = places;
    }
}
