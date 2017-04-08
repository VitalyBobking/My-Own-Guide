package diploma.edu.zp.guide_my_own.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;

import diploma.edu.zp.guide_my_own.R;
import diploma.edu.zp.guide_my_own.model.Place;
import diploma.edu.zp.guide_my_own.utils.CreateBitmapFromPath;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Val on 2/24/2017.
 */

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder> {

    private PublishSubject<View> mViewClickSubject = PublishSubject.create();
    public Observable<View> getViewClickedObservable() {
        return mViewClickSubject.asObservable();
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

        return new CountryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Place place = places.get(position);
        holder.tvCategory.setText(place.getPlaceName());

        holder.itemView.setTag(place);

        String path = place.getUrl_pic();

        if (path != null) {
            CreateBitmapFromPath.loadImage(path)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Bitmap>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            holder.ivPicture.setImageBitmap(bitmap);
                        }
                    });
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
            tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
        }
    }

    public void remove(int position) {
        places.remove(position);
        notifyItemRemoved(position);
    }

    public CountryAdapter(List<Place> places) {
        this.places = places;
    }


}