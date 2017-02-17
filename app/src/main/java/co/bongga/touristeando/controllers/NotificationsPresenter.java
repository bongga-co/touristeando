package co.bongga.touristeando.controllers;

import com.google.firebase.messaging.FirebaseMessaging;
import co.bongga.touristeando.interfaces.NotificationContract;
import co.bongga.touristeando.models.Notification;
import co.bongga.touristeando.utils.Constants;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by bongga on 2/13/17.
 */

public class NotificationsPresenter implements NotificationContract.Presenter {
    private final NotificationContract.View mNotificationView;
    private final FirebaseMessaging firebaseMessaging;
    private Realm db;
    private RealmList<Notification> items = new RealmList<>();

    public NotificationsPresenter(NotificationContract.View mNotificationView, FirebaseMessaging firebaseMessaging){
        this.mNotificationView = mNotificationView;
        this.firebaseMessaging = firebaseMessaging;

        db = Realm.getDefaultInstance();
        mNotificationView.setPresenter(this);
    }

    @Override
    public void start() {
        registerAppClient();
        loadNotifications();
    }

    @Override
    public void registerAppClient() {
        firebaseMessaging.subscribeToTopic(Constants.GENERAL_TOPIC);
    }

    @Override
    public void loadNotifications() {
        RealmResults<Notification> notifications = db.where(Notification.class)
                .findAll();
        if(notifications.size() > 0){
            mNotificationView.showEmptyState(false);
            items.clear();

            for(Notification notification : notifications){
                items.add(0, notification);
            }
            mNotificationView.showNotifications(items);
        }
        else{
            mNotificationView.showEmptyState(true);
        }
    }

    @Override
    public void savePushMessage(Notification notification) {
        Notification pushMessage = notification;

        db.beginTransaction();

        try{
            db.copyToRealm(pushMessage);
        }
        catch(Exception e){}

        db.commitTransaction();
        db.close();

        mNotificationView.showEmptyState(false);
        mNotificationView.popPushNotification(pushMessage);
    }
}