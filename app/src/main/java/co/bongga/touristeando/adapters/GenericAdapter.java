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

import java.util.List;
import java.util.Locale;

import co.bongga.touristeando.R;
import co.bongga.touristeando.models.Help;
import co.bongga.touristeando.models.PublicWiFi;
import co.bongga.touristeando.utils.Constants;
import co.bongga.touristeando.utils.UtilityManager;


/**
 * Created by bongga on 12/20/16.
 */

public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.WiFiHolder> {
    private Context context;
    private List<Object> eventList;
    private int flag;
    private int rowIndex = -1;

    public GenericAdapter(Context context){
        this.context = context;
    }

    public void setData(List<Object> eventList, int flag){
        if (this.eventList != eventList) {
            this.eventList = eventList;
            this.flag = flag;
            notifyDataSetChanged();
        }
    }

    public List<Object> getData(){
        return this.eventList;
    }

    public int getFlag(){
        return this.flag;
    }

    public void setRowIndex(int index) {
        this.rowIndex = index;
    }

    @Override
    public WiFiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.generic_type_row, parent, false);
        WiFiHolder holder = new WiFiHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WiFiHolder holder, int position) {
        if(flag == Constants.WIFI_FLAG){
            showPointLayout(holder, position);
        }
        else{
            showHelpLayout(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return (eventList != null) ? eventList.size() : 0;
    }

    public static class WiFiHolder extends RecyclerView.ViewHolder {
        public TextView name, category;
        public ImageView thumbnail;

        public WiFiHolder(View view){
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.gr_icon);
            name = (TextView)view.findViewById(R.id.gr_name);
            category = (TextView)view.findViewById(R.id.gr_category);
        }
    }

    private void showPointLayout(WiFiHolder holder, int position){
        PublicWiFi event = UtilityManager.objectFilter(eventList, PublicWiFi.class).get(position);

        /*if(event.getLatitud_y().getCoordinates() != null){
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
        }*/

        holder.name.setText(event.getNombre_del_sitio());
        holder.category.setText(String.format(Locale.getDefault(), "%s, %s", event.getDirecci_n(), event.getBarrio()));
    }

    private void showHelpLayout(WiFiHolder holder, int position){
        Help event = UtilityManager.objectFilter(eventList, Help.class).get(position);

        if(event.getIcon() != null && !event.getIcon().isEmpty()){
            String urlIcon = event.getIcon();

            Glide.with(context).load(urlIcon)
                    .placeholder(R.drawable.placeholder_img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnail);
        }
        else{
            holder.thumbnail.setVisibility(View.GONE);
        }

        holder.name.setText(event.getTitle());
        holder.category.setText(String.format(Locale.getDefault(), "%s %s",
                context.getString(R.string.example_hint), event.getExample()));
    }
}
