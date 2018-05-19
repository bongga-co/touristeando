package co.bongga.touristeando.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

import co.bongga.touristeando.utils.Globals;

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
        if(Globals.loggedUser != null){
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");

            Map<String, Object> data = new HashMap<>();
            data.put("token", refreshedToken);

            dbRef.child(Globals.loggedUser.getId()).updateChildren(data);
        }
    }
}
