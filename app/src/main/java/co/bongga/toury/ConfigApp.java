package co.bongga.toury;

import android.support.multidex.MultiDexApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by bongga on 1/15/17.
 */

public class ConfigApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("toury.realm")
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
