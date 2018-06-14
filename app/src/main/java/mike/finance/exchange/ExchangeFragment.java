package mike.finance.exchange;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mike.finance.CurrencyInformation;
import mike.finance.DataManager;
import mike.finance.DialogListAdapter;
import mike.finance.R;
import mike.finance.SettingsActivity;
import mike.finance.SharedPreferencesAccessor;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExchangeFragment extends Fragment implements IExchangeView {

    @BindView(R.id.initial_currency_code) TextView initialCode;
    @BindView(R.id.initial_currency_icon) ImageView initialIcon;
    @BindView(R.id.initial_currency_name) TextView initialName;
    @BindView(R.id.target_currency_code) TextView targetCode;
    @BindView(R.id.target_currency_name) TextView targetName;
    @BindView(R.id.target_currency_icon) ImageView targetIcon;
    @BindView(R.id.exchange_amount) TextView exchangeAmount;
    @BindView(R.id.exchange_result) TextView exchangeResult;

    @BindView(R.id.no_internet_view) View noInternetView;
    @BindView(R.id.server_side_error_view) View serverSideErrorView;

    private ExchangePresenter exchangePresenter;
    private Handler handler;
    private Runnable runnable;
    private int refreshInterval;
    private SharedPreferencesAccessor prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = new SharedPreferencesAccessor(getContext());

        DataManager dataManager = (DataManager) getActivity().getIntent().getSerializableExtra("data_manager");
        dataManager.init(view);
        exchangePresenter = new ExchangePresenter(this, dataManager);
    }

    private void startAutoRefresh() {
        handler.postDelayed(runnable, refreshInterval);
    }

    public void onRefresh() {
        exchangePresenter.refreshData(initialCode.getText().toString(), targetCode.getText().toString(),
                exchangeAmount.getText().toString());
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
        exchangePresenter.onDestroy();
        super.onDestroy();
    }

    @OnClick({R.id.initial_currency_code, R.id.target_currency_code, R.id.exchange_switcher,
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six,
            R.id.seven, R.id.eight, R.id.nine, R.id.dot, R.id.clear_last, R.id.clear_all})
    public void onClick(View view) {
        exchangePresenter.onButtonClicked(view.getId(), initialCode, targetCode, exchangeAmount);
    }

    @Override
    public void displayResult(CurrencyInformation initialCurrency, CurrencyInformation targetCurrency) {
        initialIcon.setImageResource(initialCurrency.getIcon());
        initialCode.setText(initialCurrency.getCode());
        initialName.setText(initialCurrency.getName());
        exchangeAmount.setText(initialCurrency.getRate());
        targetIcon.setImageResource(targetCurrency.getIcon());
        targetCode.setText(targetCurrency.getCode());
        targetName.setText(targetCurrency.getName());
        exchangeResult.setText(targetCurrency.getRate());
    }

    @Override
    public void goToSettings() {
        startActivity(new Intent(getContext(), SettingsActivity.class));
    }

    @Override
    public void showClearFields() {
        exchangeAmount.setText("0");
        exchangeResult.setText("0");
    }

    @Override
    public void showDot(String amount) {
        exchangeAmount.setText(amount);
    }

    @Override
    public void buildDialogCurrencyList(List<CurrencyInformation> list, TextView textView) {
        createDialogAlert(list, textView);
    }

    @Override
    public void showDataRefreshedToast() {
        Toast.makeText(getContext(), "Data refreshed!", Toast.LENGTH_SHORT).show();
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
    public void showSystemErrorToast() {
        Toast.makeText(getContext(), "Maximum field length is reached!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.exchange_toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        exchangePresenter.onOptionsMenuButtonClicked(item, initialCode, targetCode, exchangeAmount);
        return super.onOptionsItemSelected(item);
    }


    private void createDialogAlert(List<CurrencyInformation> dialogCurrencyList, TextView textView) {
        View alertDialogView = getLayoutInflater().inflate(R.layout.dialog_currency_list, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setView(alertDialogView);
        if (textView.getId() == R.id.initial_currency_code) {
            alertBuilder.setTitle("Choose initial currency");
        } else {
            alertBuilder.setTitle("Choose target currency");
        }
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        ListView dialogListView = alertDialogView.findViewById(R.id.dialog_list_view);
        DialogListAdapter dialogListAdapter = new DialogListAdapter(getContext(), dialogCurrencyList);
        dialogListView.setAdapter(dialogListAdapter);

        dialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView codeTextView = view.findViewById(R.id.dialog_currency_code);
                String code = codeTextView.getText().toString();
                textView.setText(code);
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
