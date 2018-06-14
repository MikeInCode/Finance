package mike.finance;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.Map;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferencesAccessor prefsManager;
        private SharedPreferences preferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            prefsManager = new SharedPreferencesAccessor(getActivity());
            preferences = getPreferenceScreen().getSharedPreferences();

            ListPreference autoRefresh = (ListPreference) findPreference(getString(R.string.auto_refresh));
            autoRefresh.setNegativeButtonText("Cancel");
            setSummary(autoRefresh);

            ListPreference numberPrecision = (ListPreference) findPreference(getString(R.string.number_precision));
            numberPrecision.setNegativeButtonText("Cancel");
            setSummary(numberPrecision);

            ListPreference newsPeriod = (ListPreference) findPreference(getString(R.string.news_period));
            newsPeriod.setNegativeButtonText("Cancel");
            setSummary(newsPeriod);

            ListPreference newsSearchingKeyword= (ListPreference) findPreference(getString(R.string.news_searching_keyword));
            newsSearchingKeyword.setNegativeButtonText("Cancel");
            setSummary(newsSearchingKeyword);

            Preference clearFavorites = findPreference(getString(R.string.clear_favorites));
            clearFavorites.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle("Clear favorites list?");
                    alertBuilder.setMessage("Attention! Your list of favorite currencies will be cleared!");
                    alertBuilder.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Map<String, ?> allEntries = preferences.getAll();
                            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                                if (entry.getKey().contains("_is_favorite")) {
                                    preferences.edit().putBoolean(entry.getKey(), false).apply();
                                }
                            }
                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    alertBuilder.create().show();
                    return true;
                }
            });

            Preference resetSettings = findPreference(getString(R.string.reset_settings));
            resetSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle("Reset all settings?");
                    alertBuilder.setMessage("Attention! All settings will be reset to default!");
                    alertBuilder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            preferences.edit().clear().apply();
                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    alertBuilder.create().show();
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            preferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            preferences.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);
            if (pref != null) {
                setSummary(pref);
            }
        }

        private void setSummary(Preference pref) {
            if (pref.getKey().equals(getString(R.string.auto_refresh))) {
                switch (prefsManager.getAutoRefreshValue()) {
                    case "off":
                        pref.setSummary("Off");
                        break;
                    case "30000":
                        pref.setSummary("Every 30 seconds");
                        break;
                    case "10000":
                        pref.setSummary("Every minute");
                        break;
                    case "120000":
                        pref.setSummary("Every 2 minutes");
                        break;
                    case "180000":
                        pref.setSummary("Every 3 minutes");
                        break;
                    case "240000":
                        pref.setSummary("Every 4 minutes");
                        break;
                    case "300000":
                        pref.setSummary("Every 5 minutes");
                        break;
                }
            } else if (pref.getKey().equals(getString(R.string.number_precision))) {
                switch (prefsManager.getNumberPrecisionValue()) {
                    case "1":
                        pref.setSummary("1 digit after dot");
                        break;
                    case "2":
                        pref.setSummary("2 digits after dot");
                        break;
                    case "3":
                        pref.setSummary("3 digits after dot");
                        break;
                    case "4":
                        pref.setSummary("4 digits after dot");
                        break;
                }
            } else if (pref.getKey().equals(getString(R.string.news_period))) {
                switch (prefsManager.getNewsPeriodValue()) {
                    case "1":
                        pref.setSummary("Last day");
                        break;
                    case "3":
                        pref.setSummary("Last 3 days");
                        break;
                    case "7":
                        pref.setSummary("Last 7 days");
                        break;
                }
            } else if (pref.getKey().equals(getString(R.string.news_searching_keyword))) {
                switch (prefsManager.getNewsSearchingKeywordValue()) {
                    case "finance":
                        pref.setSummary("Finance");
                        break;
                    case "business":
                        pref.setSummary("Business");
                        break;
                    case "economy":
                        pref.setSummary("Economy");
                        break;
                    case "stocks":
                        pref.setSummary("Stocks");
                        break;
                    case "trading":
                        pref.setSummary("Trading");
                        break;
                    case "cryptocurrency":
                        pref.setSummary("Cryptocurrency");
                        break;
                }
            }
        }
    }
}