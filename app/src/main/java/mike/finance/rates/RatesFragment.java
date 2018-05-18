package mike.finance.rates;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import mike.finance.DialogListAdapter;
import mike.finance.CurrencyInformation;
import mike.finance.DataManager;
import mike.finance.R;
import mike.finance.SettingsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RatesFragment extends Fragment {

    @BindView(R.id.rates_list_refresh) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rates_list_view) ListView ratesListView;
    @BindView(R.id.currency_not_found_view) TextView currencyNotFoundView;
    @BindString(R.string.base_currency) String baseCurrencyKey;
    @BindString(R.string.show_favorites) String showFavoritesKey;
    @BindString(R.string.sorting_direction) String sortingDirectionKey;
    @BindString(R.string.sorting_type) String sortingTypeKey;
    @BindString(R.string.auto_refresh) String autoRefreshKey;

    private DataManager dataManager;
    private SharedPreferences preferences;
    private Handler handler;
    private Runnable runnable;
    private int refreshInterval;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_rates, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataManager = (DataManager) getActivity().getIntent().getSerializableExtra("data_manager");
        dataManager.getRefreshedRates();

        refreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
                return ratesListView.getFirstVisiblePosition() != 0;
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataManager.getRefreshedRates();
                refreshLayout.setRefreshing(false);
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (!preferences.getString(autoRefreshKey, "off").equals("off")) {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    dataManager.getRefreshedRates();
                    Toast.makeText(getContext(), "Data refreshed!", Toast.LENGTH_SHORT).show();
                    startAutoRefresh();
                }
            };
            refreshInterval = Integer.valueOf(preferences.getString(autoRefreshKey, "off"));
            startAutoRefresh();
        }
    }

    private void startAutoRefresh() {
        handler.postDelayed(runnable, refreshInterval);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            handler.removeCallbacks(runnable);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.rates_toolbar_menu, menu);

        MenuItem changeCurrencyBtn = menu.findItem(R.id.base_currency_btn);
        changeCurrencyBtn.setTitle(preferences.getString(baseCurrencyKey, "UAH"));

        MenuItem searchBtn = menu.findItem(R.id.search_btn);
        SearchView searchView = (SearchView) searchBtn.getActionView();
        searchView.setQueryHint("Search currency");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (dataManager.getAdapter().filter(newText)) {
                    currencyNotFoundView.setVisibility(View.INVISIBLE);
                } else {
                    currencyNotFoundView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        MenuItem showFavBtn = menu.findItem(R.id.show_only_favorites_btn);
        if (preferences.getBoolean(showFavoritesKey, false)) {
            showFavBtn.setChecked(true);
        } else {
            showFavBtn.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.base_currency_btn:
                View alertDialogView = getLayoutInflater().inflate(R.layout.dialog_currency_list, null);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setView(alertDialogView);
                alertBuilder.setTitle("Choose base currency");
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();

                DialogListAdapter dialogListAdapter = new DialogListAdapter(getContext(), dataManager.getDialogCurrencyList());
                ListView dialogListView = alertDialogView.findViewById(R.id.dialog_list_view);
                dialogListView.setAdapter(dialogListAdapter);

                dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        CurrencyInformation currency = (CurrencyInformation) dialogListView.getItemAtPosition(i);
                        preferences.edit().putString(baseCurrencyKey, currency.getCode()).apply();
                        item.setTitle(currency.getCode());
                        alertDialog.dismiss();
                        dataManager.getRefreshedRates();
                    }
                });

                SearchView searchView = alertDialogView.findViewById(R.id.dialog_search_view);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        dialogListAdapter.filter(newText);
                        return false;
                    }
                });

                return true;
            case R.id.refresh_btn:
                dataManager.getRefreshedRates();
                Toast.makeText(getContext(), "Rates successfully updated!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sort_btn:
                if (preferences.getBoolean(sortingDirectionKey, true)) {
                    preferences.edit().putBoolean(sortingDirectionKey, false).apply();
                } else {
                    preferences.edit().putBoolean(sortingDirectionKey, true).apply();
                }
                dataManager.getRefreshedRates();
                break;
            case R.id.sort_by_name:
                if (!preferences.getBoolean(sortingTypeKey, true)) {
                    item.setChecked(true);
                    preferences.edit().putBoolean(sortingTypeKey, true).apply();
                    dataManager.getRefreshedRates();
                }
                break;
            case R.id.sort_by_value:
                if (preferences.getBoolean(sortingTypeKey, true)) {
                    item.setChecked(true);
                    preferences.edit().putBoolean(sortingTypeKey, false).apply();
                    dataManager.getRefreshedRates();
                }
                break;
            case R.id.show_only_favorites_btn:
                if (item.isChecked()) {
                    preferences.edit().putBoolean(showFavoritesKey, false).apply();
                    item.setChecked(false);
                } else {
                    preferences.edit().putBoolean(showFavoritesKey, true).apply();
                    item.setChecked(true);
                }
                dataManager.getRefreshedRates();
                return true;
            case R.id.settings_btn:
                try {
                    handler.removeCallbacks(runnable);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}