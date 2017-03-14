package co.bongga.touristeando.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Locale;

import co.bongga.touristeando.R;
import co.bongga.touristeando.models.Place;
import co.bongga.touristeando.utils.UtilityManager;
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

        if (place.getPrice() == null || place.getPrice().getAmount() == 0) {
            holder.priceLbl.setText(context.getString(R.string.price_lbl));
            holder.price.setText(context.getString(R.string.free_label));
        }
        else if (place.getPrice().getAmount() < 0) {
            //holder.price.setVisibility(View.GONE);
            //holder.priceLbl.setVisibility(View.GONE);
            holder.priceLbl.setText(context.getString(R.string.price_lbl));
            holder.price.setText("--");
        }
        else {
            holder.priceLbl.setText(context.getString(R.string.dt_price_lbl));
            holder.price.setText(context.getString(R.string.dt_place_price, UtilityManager.setCurrencySymbol(place.getPrice().getCurrency()), UtilityManager.setCurrencyFormat(place.getPrice())));
        }

        if(place.getDistance() != 0){
            double distance = place.getDistance();
            if (distance < 1) {
                holder.distance.setText(String.format(Locale.getDefault(), "%.0f mts", distance * 1000));
            }
            else {
                holder.distance.setText(String.format(Locale.getDefault(), "%.2f kms", distance));
            }
        }
        else {
            holder.distance.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    public static class PlaceHolder extends RecyclerView.ViewHolder {
        public TextView name, category, price, distance, priceLbl;
        public ImageView thumbnail;

        public PlaceHolder(View view){
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.event_icon);
            name = (TextView)view.findViewById(R.id.event_name);
            category = (TextView)view.findViewById(R.id.event_category);
            price = (TextView)view.findViewById(R.id.event_price);
            priceLbl = (TextView)view.findViewById(R.id.event_price_label);
            distance = (TextView)view.findViewById(R.id.event_distance);
        }
    }
}
