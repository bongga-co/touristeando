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
import co.bongga.touristeando.models.PublicWiFi;
import io.realm.RealmList;


/**
 * Created by bongga on 12/20/16.
 */

public class PublicWiFiAdapter extends RecyclerView.Adapter<PublicWiFiAdapter.WiFiHolder> {
    private Context context;
    private RealmList<PublicWiFi> eventList;
    private int rowIndex = -1;

    public PublicWiFiAdapter(Context context){
        this.context = context;
    }

    public void setData(RealmList<PublicWiFi> eventList){
        if (this.eventList != eventList) {
            this.eventList = eventList;
            notifyDataSetChanged();
        }
    }

    public RealmList<PublicWiFi> getData(){
        return this.eventList;
    }

    public void setRowIndex(int index) {
        this.rowIndex = index;
    }

    @Override
    public WiFiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_chat_event_type_row, parent, false);
        WiFiHolder holder = new WiFiHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WiFiHolder holder, int position) {
        PublicWiFi event = eventList.get(position);

        if(event.getLatitud_y().getCoordinates() != null){
            double lat = event.getLatitud_y().getCoordinates().get(1).getVal();
            double lng = event.getLatitud_y().getCoordinates().get(0).getVal();

            String staticMapImageUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" +
                    lat + "," + lng +
                    "&zoom=19&size=640x640&scale=2";

            Glide.with(context).load(staticMapImageUrl)
                    .placeholder(R.drawable.placeholder_img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnail);
        }
        else{
            holder.thumbnail.setVisibility(View.GONE);
        }

        holder.name.setText(event.getNombre_del_sitio());
        holder.category.setText(String.format(Locale.getDefault(), "%s, %s", event.getDirecci_n(), event.getBarrio()));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class WiFiHolder extends RecyclerView.ViewHolder {
        public TextView name, category;
        public ImageView thumbnail;

        public WiFiHolder(View view){
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.event_icon);
            name = (TextView)view.findViewById(R.id.event_name);
            category = (TextView)view.findViewById(R.id.event_category);
        }
    }
}
