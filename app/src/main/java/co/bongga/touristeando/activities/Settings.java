package co.bongga.touristeando.activities;

import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import co.bongga.touristeando.R;
import co.bongga.touristeando.models.ChatMessage;
import co.bongga.touristeando.utils.Globals;
import co.bongga.touristeando.utils.PreferencesManager;
import io.realm.Realm;

public class Settings extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        private PreferencesManager sharedPreferences;
        private EditTextPreference editTextPreference;
        private Preference clearHistory;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            sharedPreferences = new PreferencesManager(getActivity());

            editTextPreference = (EditTextPreference) findPreference("pref_default_distance");
            clearHistory = findPreference("pref_clear_all");

            clearHistory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle(getString(R.string.pref_clear_all))
                        .setMessage(getString(R.string.pref_clear_all_dialog_msg))
                        .setCancelable(false);

                    builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Realm db = Realm.getDefaultInstance();

                            db.beginTransaction();
                            db.where(ChatMessage.class).findAll().deleteAllFromRealm();
                            db.commitTransaction();

                            Globals.chatItems.clear();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    final AlertDialog alert = builder.create();
                    alert.show();

                    return false;
                }
            });

            int distance = sharedPreferences.getDistance();
            if(distance != 0){
                editTextPreference.setText(String.valueOf(distance));
                editTextPreference.setSummary(String.valueOf(distance) + " kms");
            }

            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object distance) {
                    int d = Integer.parseInt(distance.toString());
                    if(d < 1){
                        d = 1;
                    }
                    else if(d > 10){
                        d = 10;
                    }

                    sharedPreferences.setDefaultDistance(d);
                    editTextPreference.setText(String.valueOf(d));
                    editTextPreference.setSummary(String.valueOf(d) + " kms");

                    return false;
                }
            });
        }
    }
}
