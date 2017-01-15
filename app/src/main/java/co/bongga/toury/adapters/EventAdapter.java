package co.bongga.toury.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.bongga.toury.R;
import co.bongga.toury.models.Event;

/**
 * Created by spval on 14/01/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private Context context;
    private ArrayList<Event> chatList;

    public EventAdapter(Context context, ArrayList<Event> chatList){
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);

        return new EventHolder(rootView);
    }

    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        Event event = chatList.get(position);
        holder.name.setText(event.getName());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public TextView name;

        EventHolder(View view){
            super(view);

            name = (TextView) view.findViewById(R.id.name);
        }
    }
}
