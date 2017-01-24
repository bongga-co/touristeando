package co.bongga.toury.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
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
import co.bongga.toury.activities.EventDetail;
import co.bongga.toury.activities.PlaceDetail;
import co.bongga.toury.interfaces.RecyclerClickListener;
import co.bongga.toury.models.ChatMessage;
import co.bongga.toury.models.Event;
import co.bongga.toury.models.Place;
import co.bongga.toury.utils.Globals;
import co.bongga.toury.utils.RecyclerItemClickListener;
import io.realm.RealmList;

/**
 * Created by spval on 14/01/2017.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private static ArrayList<ChatMessage> chatList;
    private static RealmList innerEventList;
    private static RealmList innerPlacesList;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatList){
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        ChatMessage msg = chatList.get(position);

        if(msg.getLayout_type() == ChatMessage.IMAGE_TYPE){
            type = ChatMessage.IMAGE_TYPE;
        }
        else if(msg.getLayout_type() == ChatMessage.MAP_TYPE){
            type = ChatMessage.MAP_TYPE;
        }
        else if(msg.getLayout_type() == ChatMessage.EVENT_TYPE){
            type = ChatMessage.EVENT_TYPE;
        }
        else if(msg.getLayout_type() == ChatMessage.PLACES_TYPE){
            type = ChatMessage.PLACES_TYPE;
        }
        else{
            type = ChatMessage.TEXT_TYPE;
        }

        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = null;
        RecyclerView.ViewHolder holder = null;

        if(viewType == ChatMessage.IMAGE_TYPE){
            rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_event_type, parent, false);

            holder = new ImageMessageHolder(rootView);
        }
        else if(viewType == ChatMessage.MAP_TYPE){
            rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_event_type, parent, false);

            holder = new MapMessageHolder(rootView);
        }
        else if(viewType == ChatMessage.EVENT_TYPE){
            rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_event_type, parent, false);

            holder = new ListEventsMessageHolder(rootView);
        }
        else if(viewType == ChatMessage.PLACES_TYPE){
            rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_event_type, parent, false);

            holder = new ListPlacesMessageHolder(rootView);
        }
        else{
            //Right Left Layout
            rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat, parent, false);

            holder = new TextMessageHolder(rootView);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genHolder, int position) {
        ChatMessage msg = chatList.get(position);

        if(genHolder instanceof ImageMessageHolder){
            ImageMessageHolder holder = (ImageMessageHolder) genHolder;
        }
        else if(genHolder instanceof MapMessageHolder){
            MapMessageHolder holder = (MapMessageHolder) genHolder;
        }
        else if(genHolder instanceof ListEventsMessageHolder){
            ListEventsMessageHolder holder = (ListEventsMessageHolder) genHolder;
            innerEventList = chatList.get(position).getEvent();

            holder.eventAdapter.setData(innerEventList);
            holder.eventAdapter.setRowIndex(position);
        }
        else if(genHolder instanceof ListPlacesMessageHolder){
            ListPlacesMessageHolder holder = (ListPlacesMessageHolder) genHolder;

            innerPlacesList = chatList.get(position).getPlace();

            holder.placeAdapter.setData(innerPlacesList);
            holder.placeAdapter.setRowIndex(position);
        }
        else{
            TextMessageHolder holder = (TextMessageHolder) genHolder;

            holder.name.setText(msg.getMessage());
            if (msg.isSelf()) {
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
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class TextMessageHolder extends RecyclerView.ViewHolder {
        public LinearLayout itemWrapper;
        public ImageView icon;
        public TextView name;

        TextMessageHolder(View view){
            super(view);

            itemWrapper = (LinearLayout) view.findViewById(R.id.itemWrapper);
            icon = (ImageView) view.findViewById(R.id.chat_user_icon);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    public static class ImageMessageHolder extends RecyclerView.ViewHolder {
        public LinearLayout itemWrapper;
        public ImageView icon;
        public TextView name;

        ImageMessageHolder(View view){
            super(view);

            itemWrapper = (LinearLayout) view.findViewById(R.id.itemWrapper);
            icon = (ImageView) view.findViewById(R.id.chat_user_icon);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    public static class MapMessageHolder extends RecyclerView.ViewHolder {
        public LinearLayout itemWrapper;
        public ImageView icon;
        public TextView name;

        MapMessageHolder(View view){
            super(view);

            itemWrapper = (LinearLayout) view.findViewById(R.id.itemWrapper);
            icon = (ImageView) view.findViewById(R.id.chat_user_icon);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    public static class ListEventsMessageHolder extends RecyclerView.ViewHolder {
        private EventAdapter eventAdapter;

        ListEventsMessageHolder(View view){
            super(view);

            final Context context = itemView.getContext();

            RecyclerView eventList = (RecyclerView) itemView.findViewById(R.id.eventList);
            eventList.setHasFixedSize(true);
            eventList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            eventAdapter = new EventAdapter(context);
            eventList.setAdapter(eventAdapter);

            eventList.addOnItemTouchListener(new RecyclerItemClickListener(context, eventList, new RecyclerClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Event newEvent = (Event) innerEventList.get(position);
                    Globals.currentEvent = newEvent;

                    context.startActivity(new Intent(context, EventDetail.class));
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }
    }

    public static class ListPlacesMessageHolder extends RecyclerView.ViewHolder {
        private PlaceAdapter placeAdapter;

        ListPlacesMessageHolder(View view){
            super(view);

            final Context context = itemView.getContext();

            RecyclerView eventList = (RecyclerView) itemView.findViewById(R.id.eventList);
            eventList.setHasFixedSize(true);
            eventList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            placeAdapter = new PlaceAdapter(context);
            eventList.setAdapter(placeAdapter);

            eventList.addOnItemTouchListener(new RecyclerItemClickListener(context, eventList, new RecyclerClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Place newPlace = (Place) innerPlacesList.get(position);
                    Globals.currentPlace = newPlace;

                    context.startActivity(new Intent(context, PlaceDetail.class));
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }
    }
}
