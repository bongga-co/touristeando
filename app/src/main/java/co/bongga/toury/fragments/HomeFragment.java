package co.bongga.toury.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.bongga.toury.R;
import co.bongga.toury.adapters.EventAdapter;
import co.bongga.toury.models.Event;
import co.bongga.toury.utils.DividerItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView chatList;
    private EventAdapter eventAdapter;
    private ArrayList<Event> chatItems = new ArrayList();

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        chatList = (RecyclerView) view.findViewById(R.id.chatList);

        eventAdapter = new EventAdapter(getActivity(), chatItems);

        chatList.setHasFixedSize(true);
        chatList.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatList.setItemAnimator(new DefaultItemAnimator());
        chatList.addItemDecoration(new DividerItem(getActivity(), LinearLayoutManager.VERTICAL));
        chatList.setAdapter(eventAdapter);

        didGetChatList();

        return view;
    }

    private void didGetChatList(){
        Event event = new Event("Hola");
        chatItems.add(event);

        Event event2 = new Event("Hola Tourister");
        chatItems.add(event2);

        eventAdapter.notifyDataSetChanged();
    }
}
