package mike.finance.rates;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.Toast;

import mike.finance.CurrencyInformation;
import mike.finance.DataManager;
import mike.finance.DialogAdapter;
import mike.finance.R;
import mike.finance.SettingsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RatesFragment extends Fragment {

    private DataManager dataManager;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_rates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataManager = (DataManager) getActivity().getIntent().getSerializableExtra("data_manager");
        dataManager.getRefreshedRates();

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.rates_list_refresh);
        refreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
                ListView ratesListView = view.findViewById(R.id.rates_list_view);
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.rates_toolbar_menu, menu);

        MenuItem changeCurrencyBtn = menu.findItem(R.id.base_currency_btn);
        changeCurrencyBtn.setTitle(preferences.getString(getString(R.string.base_currency), "UAH"));

        MenuItem searchBtn = menu.findItem(R.id.search_btn);
        SearchView searchView = (SearchView) searchBtn.getActionView();
        searchView.setQueryHint("Find currency");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dataManager.getAdapter().filter(newText);
                return false;
            }
        });

        MenuItem showFavBtn = menu.findItem(R.id.show_only_favorites_btn);
        if (preferences.getBoolean(getString(R.string.show_favorites), false)) {
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

                DialogAdapter dialogAdapter = new DialogAdapter(getContext(), dataManager.getDialogCurrencyList());
                ListView dialogListView = alertDialogView.findViewById(R.id.dialog_list_view);
                dialogListView.setAdapter(dialogAdapter);

                dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        CurrencyInformation currency = (CurrencyInformation) dialogListView.getItemAtPosition(i);
                        preferences.edit().putString(getString(R.string.base_currency), currency.getCode()).apply();
                        item.setTitle(currency.getCode());
                        alertDialog.dismiss();
                        Toast.makeText(getContext(), "Base currency successfully changed!",
                                Toast.LENGTH_SHORT).show();
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
                        dialogAdapter.filter(newText);
                        return false;
                    }
                });

                return true;
            case R.id.refresh_btn:
                dataManager.getRefreshedRates();
                Toast.makeText(getContext(), "Rates successfully updated!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.show_only_favorites_btn:
                if (item.isChecked()) {
                    preferences.edit().putBoolean(getString(R.string.show_favorites), false).apply();
                    item.setChecked(false);
                } else {
                    preferences.edit().putBoolean(getString(R.string.show_favorites), true).apply();
                    item.setChecked(true);
                }
                dataManager.getRefreshedRates();
                return true;
            case R.id.settings_btn:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}