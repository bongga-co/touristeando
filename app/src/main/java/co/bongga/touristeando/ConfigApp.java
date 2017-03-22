package co.bongga.touristeando;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import co.bongga.touristeando.utils.Constants;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by bongga on 1/15/17.
 */

public class ConfigApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY, Constants.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("toury.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
