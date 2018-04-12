package mike.finance;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mike.finance.rates.RatesAdapter;

public final class DataUpdater {

    private final int NO_INTERNET_ERROR = 0;
    private final int SERVER_SIDE_ERROR = 1;

    private View view;
    private Context context;
    private List<CurrencyInformation> currencyList;
    private RatesAdapter adapter;
    private SharedPreferences prefs;

    public DataUpdater(View view) {
        this.view = view;
        context = view.getContext();
        currencyList = new ArrayList<>();
        adapter = new RatesAdapter(context, currencyList);
        prefs = PreferenceManager.getDefaultSharedPreferences(view.getContext());
    }

    public void refreshData(ListView ratesListView) {
        if (!isInternetConnected()) {
            noInternetError();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET,
                //CurrencyAPI.getRatesLink() + prefs.getString("primary_currency", "UAH")
                "https://jsonplaceholder.typicode.com/albums", //Using fake link, to save request quantity
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //str - fake response
                            String str = "{\"result\":\"success\",\"timestamp\":1523281154,\"from\":\"UAH\",\"rates\":{\"UAH\":1,\"AED\":0.14130045,\"ALL\":4.04157044,\"AMD\":18.49037741,\"ANG\":0.06851424,\"AOA\":8.36963022,\"ARS\":0.77646473,\"AUD\":0.05017408,\"AZN\":0.06522099,\"BBD\":0.07709901,\"BDT\":3.19716521,\"BGN\":0.06131972,\"BHD\":0.01450611,\"BRL\":0.12992622,\"BSD\":0.03847266,\"BWP\":0.37088843,\"BYN\":0.07605385,\"CAD\":0.049164,\"CHF\":0.03684062,\"CLP\":23.29208486,\"CNY\":0.24279121,\"COP\":107.37267016,\"CZK\":0.79242657,\"DKK\":0.23271078,\"DOP\":1.9004726,\"EGP\":0.67939351,\"ETB\":1.05295665,\"EUR\":0.03124746,\"FJD\":0.07784834,\"GBP\":0.02720322,\"GEL\":0.0922206,\"GHS\":0.17071671,\"GTQ\":0.2834842,\"HKD\":0.30197922,\"HNL\":0.90812756,\"HRK\":0.23212267,\"HUF\":9.74492225,\"IDR\":529.36449136,\"ILS\":0.13551595,\"INR\":2.49975278,\"IQD\":45.57351809,\"IRR\":1456.12009238,\"ISK\":3.79359834,\"JMD\":4.7841558,\"JOD\":0.02727709,\"JPY\":4.11526592,\"KES\":3.87996032,\"KHR\":153.81062356,\"KRW\":41.06438114,\"KWD\":0.01153987,\"KZT\":12.37205652,\"LAK\":318.78367975,\"LBP\":58.0540866,\"LKR\":5.98249611,\"MAD\":0.35383858,\"MDL\":0.63033626,\"MKD\":1.92349549,\"MMK\":51.11624326,\"MUR\":1.29830639,\"MXN\":0.70558983,\"MYR\":0.14888915,\"NAD\":0.46576943,\"NGN\":13.82609374,\"NOK\":0.29962712,\"NZD\":0.0528036,\"OMR\":0.01480428,\"PAB\":0.03847266,\"PEN\":0.12456676,\"PGK\":0.12507698,\"PHP\":2.0022512,\"PKR\":4.44800947,\"PLN\":0.13102828,\"PYG\":212.65817894,\"QAR\":0.140442,\"RON\":0.14563248,\"RSD\":3.6965346,\"RUB\":2.31264056,\"SAR\":0.14427824,\"SCR\":0.51143855,\"SEK\":0.3212685,\"SGD\":0.05047824,\"THB\":1.20292432,\"TJS\":0.3397806,\"TND\":0.092848,\"TRY\":0.15588935,\"TTD\":0.25760198,\"TWD\":1.12471018,\"TZS\":86.77256711,\"USD\":0.03847266,\"UYU\":1.08781473,\"UZS\":311.20092379,\"VEF\":1900.81958891,\"VND\":877.15689502,\"XAF\":20.88626374,\"XCD\":0.10445253,\"XOF\":20.78143874,\"XPF\":3.70669758,\"ZAR\":0.46590015,\"ZMW\":0.36197823}}";
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode jsonChild = mapper.readTree(str).get("rates");
                            if (jsonChild == null) {
                                serverSideError();
                                return;
                            }
                            if (currencyList.size() != jsonChild.size()) {
                                currencyList.clear();
                                Map<String, String> map = mapper.readValue(jsonChild.toString(), new TypeReference<TreeMap<String, String>>() {
                                });
                                for (Map.Entry<String, String> entry : map.entrySet()) {
                                    // key - currency abbreviation; value - currency rate
                                    String key = entry.getKey();
                                    String value = entry.getValue();
                                    CurrencyInformation currency = new CurrencyInformation(key, value);
                                    String[] currencyExtra = getExtraInfo(key);
                                    // currencyExtra[0] - currency full name; currencyExtra[1] - currency icon
                                    currency.setName(currencyExtra[0]).setIcon(currencyExtra[1]);
                                    currencyList.add(currency);
                                }
                            } else {
                                for (CurrencyInformation i : currencyList) {
                                    i.setRate(jsonChild.get(i.getCode()).toString());
                                }
                            }
                            successfulRequest();
                            ratesListView.setAdapter(adapter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverSideError();
                    }
                }));
    }

    public void refreshData(String fromCurrency, String toCurrency, String amount, TextView exchangeResult) {
        if (!isInternetConnected()) {
            noInternetError();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET,
                //CurrencyAPI.getExchangeLink() + fromCurrency + "/" + toCurrency
                "https://jsonplaceholder.typicode.com/albums",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //str - fake response
                            String str = "{\"result\":\"success\",\"timestamp\":1523286214,\"from\":\"USD\",\"to\":\"UAH\",\"rate\":25.99233516}";
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode currencyRate = mapper.readTree(str).get("rate");
                            if (currencyRate == null) {
                                serverSideError();
                                return;
                            }
                            String[] fromCurrencyExtra = getExtraInfo(fromCurrency);
                            TextView fromCurrencyCode = view.findViewById(R.id.from_currency_code);
                            TextView fromCurrencyName = view.findViewById(R.id.from_currency_name);
                            ImageView fromCurrencyIcon = view.findViewById(R.id.from_currency_icon);

                            fromCurrencyCode.setText(fromCurrency);
                            fromCurrencyName.setText(fromCurrencyExtra[0]);
                            fromCurrencyIcon.setImageResource(context.getResources()
                                    .getIdentifier(fromCurrencyExtra[1], "drawable", context.getPackageName()));

                            String[] toCurrencyExtra = getExtraInfo(toCurrency);
                            TextView toCurrencyCode = view.findViewById(R.id.to_currency_code);
                            TextView toCurrencyName = view.findViewById(R.id.to_currency_name);
                            ImageView toCurrencyIcon = view.findViewById(R.id.to_currency_icon);

                            toCurrencyCode.setText(toCurrency);
                            toCurrencyName.setText(toCurrencyExtra[0]);
                            toCurrencyIcon.setImageResource(context.getResources()
                                    .getIdentifier(toCurrencyExtra[1], "drawable", context.getPackageName()));

                            successfulRequest();
                            exchangeResult.setText(MathOperations
                                    .calculateExchangeResult(prefs, amount, currencyRate.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverSideError();
                    }
                }));
    }

    private String[] getExtraInfo(String currencyCode) {
        int code = view.getResources().getIdentifier(currencyCode, "array", context.getPackageName());
        return view.getResources().getStringArray(code);
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void successfulRequest() {
        view.findViewById(R.id.no_internet_view).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.server_side_error_view).setVisibility(View.INVISIBLE);
    }

    private void noInternetError() {
        View viewToShow = view.findViewById(R.id.no_internet_view);
        viewToShow.setVisibility(View.VISIBLE);
        viewToShow.bringToFront();
        showSnackbar(NO_INTERNET_ERROR);
    }

    private void serverSideError() {
        View viewToShow = view.findViewById(R.id.server_side_error_view);
        viewToShow.setVisibility(View.VISIBLE);
        viewToShow.bringToFront();
        showSnackbar(SERVER_SIDE_ERROR);
    }

    private void showSnackbar(int responseStatus) {
        StringBuilder message = new StringBuilder();
        if (responseStatus == NO_INTERNET_ERROR) {
            message.append("No Internet connection!");
        } else if (responseStatus == SERVER_SIDE_ERROR) {
            message.append("Oops! Couldn't receive data!");
        }
        Snackbar snackbar = Snackbar.make(view, message,
                Snackbar.LENGTH_LONG);
        TextView sbText = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        sbText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.show();
    }
}


