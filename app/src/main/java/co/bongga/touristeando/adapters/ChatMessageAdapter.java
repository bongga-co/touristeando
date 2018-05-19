package co.bongga.touristeando.adapters;

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
import java.util.List;

import co.bongga.touristeando.R;
import co.bongga.touristeando.activities.PlaceDetail;
import co.bongga.touristeando.interfaces.RecyclerClickListener;
import co.bongga.touristeando.models.ChatMessage;
import co.bongga.touristeando.models.Place;
import co.bongga.touristeando.utils.Constants;
import co.bongga.touristeando.utils.Globals;
import co.bongga.touristeando.utils.RecyclerItemClickListener;
import co.bongga.touristeando.utils.UtilityManager;
import io.realm.RealmList;

/**
 * Created by spval on 14/01/2017.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ChatMessage> chatList;

    public ChatMessageAdapter(ArrayList<ChatMessage> chatList){
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
        else if(msg.getLayout_type() == ChatMessage.GENERIC_TYPE){
            type = ChatMessage.GENERIC_TYPE;
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
        View rootView;
        RecyclerView.ViewHolder holder;

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
        else if(viewType == ChatMessage.GENERIC_TYPE){
            rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_event_type, parent, false);

            holder = new GenericMessageHolder(rootView);
        }
        else if(viewType == ChatMessage.PLACES_TYPE){
            rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_event_type, parent, false);

            holder = new ListPlacesMessageHolder(rootView);
        }
        else{
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
        else if(genHolder instanceof GenericMessageHolder){
            GenericMessageHolder holder = (GenericMessageHolder) genHolder;
            List<Object> events;

            if(chatList.get(position).getFlag() == Constants.WIFI_FLAG){
                events = UtilityManager.objectFilter(chatList.get(position).getEvent(), Object.class);
                holder.genericAdapter.setData(events, Constants.WIFI_FLAG);
            }
            else {
                events = UtilityManager.objectFilter(chatList.get(position).getHelp(), Object.class);
                holder.genericAdapter.setData(events, Constants.HELP_FLAG);
            }

            holder.genericAdapter.setRowIndex(position);
        }
        else if(genHolder instanceof ListPlacesMessageHolder){
            ListPlacesMessageHolder holder = (ListPlacesMessageHolder) genHolder;

            RealmList<Place> places = chatList.get(position).getPlace();

            holder.placeAdapter.setData(places);
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

    public static class GenericMessageHolder extends RecyclerView.ViewHolder {
        private GenericAdapter genericAdapter;

        GenericMessageHolder(View view){
            super(view);

            final Context context = itemView.getContext();

            RecyclerView eventList = (RecyclerView) itemView.findViewById(R.id.eventList);
            eventList.setHasFixedSize(true);
            eventList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            genericAdapter = new GenericAdapter(context);
            eventList.setAdapter(genericAdapter);
        }
    }

    public static class ListPlacesMessageHolder extends RecyclerView.ViewHolder {
        private PlaceAdapter placeAdapter;

        ListPlacesMessageHolder(View view){
            super(view);

            final Context context = itemView.getContext();

            RecyclerView eventList = (RecyclerView) itemView.findViewById(R.id.eventList);
            eventList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            eventList.setHasFixedSize(true);
            placeAdapter = new PlaceAdapter(context);
            eventList.setAdapter(placeAdapter);

            eventList.addOnItemTouchListener(new RecyclerItemClickListener(context, eventList, new RecyclerClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Place newPlace = placeAdapter.getData().get(position);
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
