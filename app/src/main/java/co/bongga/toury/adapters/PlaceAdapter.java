package co.bongga.toury.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import co.bongga.toury.R;
import co.bongga.toury.models.Place;
import io.realm.RealmList;

/**
 * Created by bongga on 1/24/17.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {
    private Context context;
    private RealmList<Place> placesList;
    private int rowIndex = -1;

    public PlaceAdapter(Context context){
        this.context = context;
    }

    public void setData(RealmList<Place> placesList){
        if (this.placesList != placesList) {
            this.placesList = placesList;
            notifyDataSetChanged();
        }
    }

    public RealmList<Place> getData(){
        return this.placesList;
    }

    public void setRowIndex(int index) {
        this.rowIndex = index;
    }

    @Override
    public PlaceAdapter.PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_event_type_row, parent, false);
        PlaceAdapter.PlaceHolder holder = new PlaceAdapter.PlaceHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PlaceAdapter.PlaceHolder holder, int position) {
        Place place = placesList.get(position);

        Glide.with(context).load(place.getThumbnail())
                .placeholder(R.drawable.placeholder_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);

        holder.name.setText(place.getName());
        holder.category.setText(place.getCategory());
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public static class PlaceHolder extends RecyclerView.ViewHolder {
        public TextView name, category;
        public ImageView thumbnail;

        public PlaceHolder(View view){
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.event_icon);
            name = (TextView)view.findViewById(R.id.event_name);
            category = (TextView)view.findViewById(R.id.event_category);
        }
    }
}
