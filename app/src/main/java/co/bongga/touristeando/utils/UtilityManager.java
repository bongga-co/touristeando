package co.bongga.touristeando.utils;

/**
 * Created by spval on 15/01/2017.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.bongga.touristeando.interfaces.SnackCallback;
import co.bongga.touristeando.models.Coordinate;
import co.bongga.touristeando.models.RealmDouble;
import io.realm.RealmList;
import io.realm.RealmObject;

public class UtilityManager {
    private static UtilityManager ourInstance = new UtilityManager();

    private UtilityManager() {}

    public static UtilityManager getInstance() {
        return ourInstance;
    }

    public static boolean isConnected(Context context){
        boolean isDeviceConnected = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                isDeviceConnected = true;
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                isDeviceConnected = true;
            }
        }
        else {
            isDeviceConnected = false;
        }

        return isDeviceConnected;
    }

    public static void showMessage (View view, String msg){
        Snackbar snack = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snack.show();
    }

    public static void showMessageWithAction(View view, String msg, String btnLabel, final SnackCallback callback){
        Snackbar snackbar = Snackbar
                .make(view, msg, Snackbar.LENGTH_INDEFINITE)
                .setAction(btnLabel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.didTapSnackAction();
                    }
                });

        snackbar.show();
    }

    public static ProgressDialog showLoader(Context context, String msg){
        ProgressDialog loader = new ProgressDialog(context);
        loader.setMessage(msg);
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);

        return loader;
    }

    public static long parseDate(String stringDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        Date date;
        long result = -1;

        try {
            date = format.parse(stringDate);
            result = date.getTime();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void hideKeyboard(Activity context){
        InputMethodManager inputManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String removeAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    public static double convertKmToMi(double kilometers) {
        double miles = kilometers * 0.621;
        return miles;
    }

    public static boolean validateEmail(final String emailID){
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailID);

        return matcher.matches();
    }

    public static boolean validateName(final String name){
        final String NAME_PATTERN = "^[a-zA-z]+([ '-][a-zA-Z]+)*";

        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }

    public static double calculateDistance(Coordinate origin, Coordinate destination) {
        double theta = origin.getLongitude() - destination.getLongitude();
        double dist = Math.sin(deg2rad(origin.getLatitude()))
                * Math.sin(deg2rad(destination.getLatitude()))
                + Math.cos(deg2rad(origin.getLatitude()))
                * Math.cos(deg2rad(destination.getLatitude()))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    public static double calculateDistance2(Coordinate origin, Coordinate destination){
        double R = 6371000f; // Radius of the earth in m
        double dLat = (origin.getLatitude() - destination.getLatitude()) * Math.PI / 180f;
        double dLon = (origin.getLongitude() - destination.getLongitude()) * Math.PI / 180f;

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(origin.getLatitude() * Math.PI / 180f) * Math.cos(destination.getLatitude() * Math.PI / 180f) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2f * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;

        return d/1000f;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static Gson doubleBuilder(){
        Type tokenInt = new TypeToken<RealmList<RealmDouble>>(){}.getType();

        Gson gson = new GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getDeclaringClass().equals(RealmObject.class);
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .registerTypeAdapter(tokenInt, new TypeAdapter<RealmList<RealmDouble>>() {

                @Override
                public void write(JsonWriter out, RealmList<RealmDouble> value) throws IOException {
                    // Ignore
                }

                @Override
                public RealmList<RealmDouble> read(JsonReader in) throws IOException {
                    RealmList<RealmDouble> list = new RealmList<>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(new RealmDouble(in.nextDouble()));
                    }
                    in.endArray();
                    return list;
                }
            })
            .create();
        return gson;
    }
}
