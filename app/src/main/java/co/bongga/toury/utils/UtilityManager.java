package co.bongga.toury.utils;

/**
 * Created by spval on 15/01/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.bongga.toury.interfaces.SnackCallback;

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
}
