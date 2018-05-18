package mike.finance;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.Map;

import butterknife.BindString;
import butterknife.ButterKnife;


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

        @BindString(R.string.auto_refresh) String autoRefreshKey;
        @BindString(R.string.number_precision) String numberPrecisionKey;
        @BindString(R.string.clear_favorites) String clearFavoritesKey;
        @BindString(R.string.reset_settings) String resetSettingsKey;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ButterKnife.bind(this, view);

            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            Preference autoRefresh = findPreference(autoRefreshKey);
            setSummary(autoRefresh);

            Preference numberPrecision = findPreference(numberPrecisionKey);
            setSummary(numberPrecision);

            Preference clearFavorites = findPreference(clearFavoritesKey);
            clearFavorites.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle("Clear favorites list?");
                    alertBuilder.setMessage("Attention! Your list of favorite currencies will be cleared!");
                    alertBuilder.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Map<String, ?> allEntries = sharedPreferences.getAll();
                            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                                if (entry.getKey().contains(getString(R.string.is_favorite))) {
                                    sharedPreferences.edit().putBoolean(entry.getKey(), false).apply();
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

            Preference resetSettings = findPreference(resetSettingsKey);
            resetSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle("Reset all settings?");
                    alertBuilder.setMessage("Attention! All settings will be reset to default!");
                    alertBuilder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sharedPreferences.edit().clear().apply();
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
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);
            if (key.equals(autoRefreshKey)) {
                setSummary(pref);
            } else if (key.equals(numberPrecisionKey)) {
                setSummary(pref);
            }
        }

        private void setSummary(Preference pref) {
            if (pref.getKey().equals(autoRefreshKey)) {
                String value = pref.getSharedPreferences().getString(autoRefreshKey, "off");
                switch (value) {
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
            } else if (pref.getKey().equals(numberPrecisionKey)) {
                String value = pref.getSharedPreferences().getString(numberPrecisionKey, "3");
                switch (value) {
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
            }
        }
    }
}