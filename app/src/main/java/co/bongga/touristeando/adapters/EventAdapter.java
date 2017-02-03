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
import co.bongga.touristeando.R;
import co.bongga.touristeando.models.Event;
import io.realm.RealmList;


/**
 * Created by bongga on 12/20/16.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private Context context;
    private RealmList<Event> eventList;
    private int rowIndex = -1;

    public EventAdapter(Context context){
        this.context = context;
    }

    public void setData(RealmList<Event> eventList){
        if (this.eventList != eventList) {
            this.eventList = eventList;
            notifyDataSetChanged();
        }
    }

    public RealmList<Event> getData(){
        return this.eventList;
    }

    public void setRowIndex(int index) {
        this.rowIndex = index;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_chat_event_type_row, parent, false);
        EventHolder holder = new EventHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        Event event = eventList.get(position);

        Glide.with(context).load(event.getThumbnail())
                .placeholder(R.drawable.placeholder_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);

        holder.name.setText(event.getName());
        holder.category.setText(event.getCategory());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventHolder extends RecyclerView.ViewHolder {
        public TextView name, category;
        public ImageView thumbnail;

        public EventHolder(View view){
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.event_icon);
            name = (TextView)view.findViewById(R.id.event_name);
            category = (TextView)view.findViewById(R.id.event_category);
        }
    }
}
