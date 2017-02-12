package co.bongga.touristeando.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by bongga on 2/12/17.
 */

public class FCMInstanceIdService extends FirebaseInstanceIdService {
    public FCMInstanceIdService(){

    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken){

    }
}
