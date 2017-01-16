package co.bongga.toury.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        if (event.isSelf()) {
            holder.name.setBackgroundResource(R.drawable.bg_msg_you);
            holder.icon.setVisibility(View.GONE);
            holder.itemWrapper.setGravity(Gravity.END);
        }
        else {
            holder.name.setBackgroundResource(R.drawable.bg_msg_from);
            holder.icon.setVisibility(View.VISIBLE);
            holder.itemWrapper.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public LinearLayout itemWrapper;
        public ImageView icon;
        public TextView name;

        EventHolder(View view){
            super(view);

            itemWrapper = (LinearLayout) view.findViewById(R.id.itemWrapper);
            icon = (ImageView) view.findViewById(R.id.chat_user_icon);
            name = (TextView) view.findViewById(R.id.name);
        }
    }
}
