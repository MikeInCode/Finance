package mike.finance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            ListPreference p = (ListPreference) findPreference("list_preference_1");
            List<String> list = new ArrayList<>();
            list.add("fdsfsdfsdf");
            CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);

            SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();

            Preference favoriteBtnPref = findPreference("show_favorites");
            setUpFavoriteBtn(prefs, favoriteBtnPref);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);
            switch (key) {
                case "show_favorites":
                    setUpFavoriteBtn(sharedPreferences, pref);
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        private void setUpFavoriteBtn(SharedPreferences sharedPreferences, Preference pref) {
            if (!sharedPreferences.getBoolean(pref.getKey(), false)) {
                pref.setSummary("Showing all currencies");
            } else {
                pref.setSummary("Showing only favorites currencies");
            }
        }
    }
}