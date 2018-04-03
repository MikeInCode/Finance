package mike.finance;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mike.finance.rates.RatesAdapter;

public final class DataUpdater {
    private final int SUCCESSFUL = 0;
    private final int NO_INTERNET_ERROR = 1;
    private final int SERVER_SIDE_ERROR = 2;
    private final int RATES = 3;
    private final int EXCHANGE = 4;

    private Context context;
    private View view;
    private List<CurrencyInformation> currencyList;
    private RatesAdapter adapter;

    public DataUpdater(Context context, View view) {
        this.context = context;
        this.view = view;
        currencyList = new ArrayList<>();
        adapter = new RatesAdapter(context);
    }

    private void fillCurrencyList() {
        List<String> readData = FileReader.readFile(context, R.raw.currency_information);
        for (String i : readData) {
            String[] str = i.split(":");
            currencyList.add(new CurrencyInformation(str[0], str[1], str[2]));
        }
    }

    public void refreshRatesData(ListView ratesListView) {
        if (currencyList.isEmpty()) {
            fillCurrencyList();
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET,
                CurrencyAPI.getRatesLink("UAH"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setResponseStatus(SUCCESSFUL, RATES);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            for (CurrencyInformation i : currencyList) {
                                String rate = jsonObject.getJSONObject("rates")
                                        .getString(i.getCurrencyAbbreviation());
                                i.setCurrencyRate(MathOperations.buildCurrencyRate(rate));
                            }
                            adapter.setCurrencyList(currencyList).setPrimaryCurrency("UAH");
                            ratesListView.setAdapter(adapter);
                        } catch (JSONException e) {
                            setResponseStatus(SERVER_SIDE_ERROR, RATES);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setResponseStatus(NO_INTERNET_ERROR, RATES);
                    }
                }));
    }

    public void refreshRatesData(String amount, TextView exchangeResult) {
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET,
                CurrencyAPI.getExchangeLink("USD", "UAH"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setResponseStatus(SUCCESSFUL, EXCHANGE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String rate = jsonObject.getString("rate");
                            exchangeResult.setText(MathOperations.calculateExchangeResult(amount, rate));
                        } catch (JSONException e) {
                            setResponseStatus(SERVER_SIDE_ERROR, EXCHANGE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setResponseStatus(NO_INTERNET_ERROR, EXCHANGE);
                    }
                }));
    }

    private void setResponseStatus(int responseStatus, int appSection) {
        switch (responseStatus) {
            case SUCCESSFUL:
                view.findViewById(R.id.no_internet_view).setVisibility(View.GONE);
                view.findViewById(R.id.server_side_error_view).setVisibility(View.GONE);
                if (appSection == RATES) {
                    view.findViewById(R.id.rates_list_view).setVisibility(View.VISIBLE);
                } else if (appSection == EXCHANGE) {
                    view.findViewById(R.id.main_composition).setVisibility(View.VISIBLE);
                }
                break;
            case NO_INTERNET_ERROR:
                view.findViewById(R.id.no_internet_view).setVisibility(View.VISIBLE);
                view.findViewById(R.id.server_side_error_view).setVisibility(View.GONE);
                if (appSection == RATES) {
                    view.findViewById(R.id.rates_list_view).setVisibility(View.GONE);
                } else if (appSection == EXCHANGE) {
                    view.findViewById(R.id.main_composition).setVisibility(View.GONE);
                }
                Snackbar snackbar = Snackbar.make(view, "No Internet connection!",
                        Snackbar.LENGTH_LONG);
                TextView sbText = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                sbText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                snackbar.show();
                break;
            case SERVER_SIDE_ERROR:
                view.findViewById(R.id.no_internet_view).setVisibility(View.GONE);
                view.findViewById(R.id.server_side_error_view).setVisibility(View.VISIBLE);
                if (appSection == RATES) {
                    view.findViewById(R.id.rates_list_view).setVisibility(View.GONE);
                } else if (appSection == EXCHANGE) {
                    view.findViewById(R.id.main_composition).setVisibility(View.GONE);
                }
                Snackbar snackbar2 = Snackbar.make(view, "Oops! Couldn't receive data!",
                        Snackbar.LENGTH_LONG);
                TextView sbText2 = snackbar2.getView().findViewById(android.support.design.R.id.snackbar_text);
                sbText2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                snackbar2.show();
                break;
        }
    }
}

