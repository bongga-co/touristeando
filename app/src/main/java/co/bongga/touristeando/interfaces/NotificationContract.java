package co.bongga.touristeando.interfaces;

import co.bongga.touristeando.models.Notification;
import io.realm.RealmList;

/**
 * Created by bongga on 2/13/17.
 */

public interface NotificationContract {
    interface View extends BaseView<Presenter>{
        void showNotifications(RealmList<Notification> notifications);
        void showEmptyState(boolean empty);
        void popPushNotification(Notification pushMessage);
    }

    interface Presenter extends BasePresenter {
        void registerAppClient();
        void loadNotifications();
        void savePushMessage(Notification notification);
    }
}
