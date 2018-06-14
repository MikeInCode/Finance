package mike.finance.rates;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mike.finance.CurrencyInformation;
import mike.finance.DataManager;
import mike.finance.DialogListAdapter;
import mike.finance.R;
import mike.finance.SettingsActivity;
import mike.finance.SharedPreferencesAccessor;

/**
 * A simple {@link Fragment} subclass.
 */
public class RatesFragment extends Fragment implements IRatesView, SwipeRefreshLayout.OnChildScrollUpCallback,
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rates_list_view) ListView ratesListView;
    @BindView(R.id.rates_list_refresh) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.no_internet_view) View noInternetView;
    @BindView(R.id.server_side_error_view) View serverSideErrorView;
    @BindView(R.id.empty_favorites_view) View emptyFavoritesView;

    private RatesPresenter ratesPresenter;
    private RatesAdapter ratesAdapter;
    private SharedPreferencesAccessor prefs;
    private Handler handler;
    private Runnable runnable;
    private int refreshInterval;
    private MenuItem changeCurrencyBtn;

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

        prefs = new SharedPreferencesAccessor(getContext());

        DataManager dataManager = (DataManager) getActivity().getIntent().getSerializableExtra("data_manager");
        dataManager.init(view);
        ratesPresenter = new RatesPresenter(this, dataManager);
        ratesAdapter = new RatesAdapter(getContext(), new RatesAdapterCallback() {
            @Override
            public void onFavButtonClicked(View view, int position, String currencyCode) {
                ratesPresenter.addToFavorites(prefs, ((ImageButton) view), currencyCode);
            }
        });

        refreshLayout.setOnChildScrollUpCallback(this);
        refreshLayout.setOnRefreshListener(this);
    }

    public interface RatesAdapterCallback {
        void onFavButtonClicked(View view, int position, String currencyCode);
    }

    private void startAutoRefresh() {
        handler.postDelayed(runnable, refreshInterval);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();

        onRefresh();
        if (!prefs.getAutoRefreshValue().equals("off")) {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    onRefresh();
                    showDataRefreshedToast();
                    startAutoRefresh();
                }
            };
            refreshInterval = Integer.valueOf(prefs.getAutoRefreshValue());
            startAutoRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            handler.removeCallbacks(runnable);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        ratesPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
        return ratesListView.getFirstVisiblePosition() != 0;
    }

    @Override
    public void onRefresh() {
        ratesPresenter.refreshData();
    }

    @Override
    public void displayResult(List<CurrencyInformation> list) {
        ratesAdapter.setCurrencyList(list);
        ratesListView.setAdapter(ratesAdapter);
    }

    @Override
    public void hideRefreshingStatus() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void goToSettings() {
        startActivity(new Intent(getContext(), SettingsActivity.class));
    }

    @Override
    public void setMenuItemChecked(MenuItem item, boolean value) {
        item.setChecked(value);
    }

    @Override
    public void buildDialogCurrencyList(List<CurrencyInformation> list) {
        createDialogAlert(list);
    }

    @Override
    public void showDataRefreshedToast() {
        Toast.makeText(getContext(), "Data refreshed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFavoriteStatus(ImageButton btn) {
        btn.setImageResource(R.drawable.ic_favorite_active);
    }

    @Override
    public void hideFavoriteStatus(ImageButton btn) {
        btn.setImageResource(R.drawable.ic_favorite_inactive);
    }

    @Override
    public void hideAllRequestErrorViews() {
        noInternetView.setVisibility(View.INVISIBLE);
        serverSideErrorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNoInternetErrorView() {
        noInternetView.setVisibility(View.VISIBLE);
        noInternetView.bringToFront();
    }

    @Override
    public void showServerSideErrorView() {
        serverSideErrorView.setVisibility(View.VISIBLE);
        serverSideErrorView.bringToFront();
    }

    @Override
    public void showEmptyFavoritesView() {
        emptyFavoritesView.setVisibility(View.VISIBLE);
        emptyFavoritesView.bringToFront();
    }

    @Override
    public void hideEmptyFavoritesView() {
        emptyFavoritesView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSystemErrorToast() {
        Toast.makeText(getActivity(),
                "Something goes wrong. App restarting could fix the issue",
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.rates_toolbar_menu, menu);
        initOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ratesPresenter.onOptionsMenuButtonClicked(prefs, item);
        return super.onOptionsItemSelected(item);
    }

    private void initOptionsMenu(Menu menu) {
        changeCurrencyBtn = menu.findItem(R.id.base_currency_btn);
        changeCurrencyBtn.setTitle(prefs.getBaseCurrencyValue());

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
                try {
                    if (ratesAdapter.filter(newText)) {
                        getView().findViewById(R.id.currency_not_found_msg).setVisibility(View.INVISIBLE);
                    } else {
                        getView().findViewById(R.id.currency_not_found_msg).setVisibility(View.VISIBLE);
                    }
                    return true;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        switch (prefs.getRatesSortingTypeValue()) {
            case 0:
                menu.findItem(R.id.name_natural).setChecked(true);
                break;
            case 1:
                menu.findItem(R.id.name_reverse).setChecked(true);
                break;
            case 2:
                menu.findItem(R.id.value_natural).setChecked(true);
                break;
            case 3:
                menu.findItem(R.id.value_reverse).setChecked(true);
                break;
        }

        MenuItem showFavBtn = menu.findItem(R.id.show_only_favorites_btn);
        if (prefs.getShowFavoritesValue()) {
            showFavBtn.setChecked(true);
        } else {
            showFavBtn.setChecked(false);
        }
    }

    private void createDialogAlert(List<CurrencyInformation> alertDialogList) {
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

        DialogListAdapter dialogListAdapter = new DialogListAdapter(getContext(), alertDialogList);
        ListView dialogListView = alertDialogView.findViewById(R.id.dialog_list_view);
        dialogListView.setAdapter(dialogListAdapter);

        dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView codeTextView = view.findViewById(R.id.dialog_currency_code);
                String code = codeTextView.getText().toString();
                prefs.setBaseCurrencyValue(code);
                changeCurrencyBtn.setTitle(code);
                alertDialog.dismiss();
                onRefresh();
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
                if (dialogListAdapter.filter(newText)) {
                    alertDialogView.findViewById(R.id.currency_not_found_msg).setVisibility(View.INVISIBLE);
                } else {
                    alertDialogView.findViewById(R.id.currency_not_found_msg).setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }
}