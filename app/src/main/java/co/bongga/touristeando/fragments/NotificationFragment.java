package co.bongga.touristeando.fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import co.bongga.touristeando.R;
import co.bongga.touristeando.adapters.NotificationAdapter;
import co.bongga.touristeando.controllers.NotificationsPresenter;
import co.bongga.touristeando.interfaces.NotificationContract;
import co.bongga.touristeando.models.Notification;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements NotificationContract.View {
    private RecyclerView notificationList;
    private LinearLayout emptyView;
    private NotificationAdapter notificationAdapter;
    private NotificationsPresenter presenter;
    private Realm realmDB;

    public NotificationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realmDB = Realm.getDefaultInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationAdapter = new NotificationAdapter();

        emptyView = (LinearLayout) rootView.findViewById(R.id.noMessages);
        notificationList = (RecyclerView) rootView.findViewById(R.id.notificationList);
        notificationList.setHasFixedSize(true);
        notificationList.setAdapter(notificationAdapter);

        setupSwipeGesture();

        return rootView;
    }

    @Override
    public void showNotifications(RealmList<Notification> notifications) {
        notificationAdapter.replaceData(notifications);
    }

    @Override
    public void showEmptyState(boolean empty) {
        notificationList.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void popPushNotification(Notification pushMessage) {
        notificationAdapter.addItem(pushMessage);
        notificationList.scrollToPosition(0);
    }

    @Override
    public void setPresenter(NotificationContract.Presenter _presenter) {
        if (_presenter != null) {
            presenter = (NotificationsPresenter) _presenter;
        }
        else {
            throw new RuntimeException(getString(R.string.generic_unexpected_error));
        }
    }

    private void setupSwipeGesture(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                Notification currentNotification = notificationAdapter.fetchItem(position);
                notificationAdapter.removeItem(position);

                final RealmResults<Notification> notificationItems = realmDB.where(Notification.class).equalTo("id", currentNotification.getId()).findAll();
                if(notificationItems != null){
                    if(notificationItems.size() > 0){
                        realmDB.beginTransaction();
                        notificationItems.deleteAllFromRealm();
                        realmDB.commitTransaction();
                    }
                    else{
                        showEmptyState(true);
                    }
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(notificationList);
    }
}