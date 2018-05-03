package mike.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mike.finance.rates.RatesAdapter;

public final class DataManager implements Serializable {

    private final int NO_INTERNET_ERROR = 0;
    private final int SERVER_SIDE_ERROR = 1;

    private View view;
    private Context context;
    private List<CurrencyInformation> currencyList;
    private List<CurrencyInformation> favCurrencyList;
    private RatesAdapter adapter;
    private SharedPreferences preferences;

    public DataManager(View view) {
        this.view = view;
        context = view.getContext();
        currencyList = new ArrayList<>();
        favCurrencyList = new ArrayList<>();
        adapter = new RatesAdapter(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public List<CurrencyInformation> getDialogCurrencyList() {
        List<CurrencyInformation> dialogCurrencyList = new ArrayList<>();
        for (CurrencyInformation i : currencyList) {
            String key = i.getCode() + context.getString(R.string.is_favorite);
            if (preferences.getBoolean(key, false)) {
                dialogCurrencyList.add(i);
            }
        }
        for (CurrencyInformation i : currencyList) {
            if (!dialogCurrencyList.contains(i)) {
                dialogCurrencyList.add(i);
            }
        }
        return dialogCurrencyList;
    }

    public RatesAdapter getAdapter() {
        return adapter;
    }

    public void getRefreshedRates() {
        if (!isInternetConnected()) {
            noInternetError();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET,
                //CurrencyAPI.getRatesLink(preferences.getString(context.getString(R.string.base_currency), "UAH")),
                "https://jsonplaceholder.typicode.com/albums", //Using fake link, to save request quantity
                new Response.Listener<String>() {
                    @SuppressLint("ResourceType")
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
                                    // key - currency code; value - currency rate
                                    String key = entry.getKey();
                                    String value = entry.getValue();
                                    CurrencyInformation currency = new CurrencyInformation(key, value);
                                    setExtraInfo(currency);
                                    currencyList.add(currency);
                                }
                            }
                            if (!preferences.getBoolean(context.getString(R.string.show_favorites), false)) {
                                for (CurrencyInformation i : currencyList) {
                                    i.setRate(jsonChild.get(i.getCode()).toString());
                                }
                                adapter.setCurrencyList(currencyList);
                            } else {
                                favCurrencyList.clear();
                                for (CurrencyInformation i : currencyList) {
                                    String key = i.getCode() + context.getString(R.string.is_favorite);
                                    if (preferences.getBoolean(key, false)) {
                                        i.setRate(jsonChild.get(i.getCode()).toString());
                                        favCurrencyList.add(i);
                                    }
                                }
                                adapter.setCurrencyList(favCurrencyList);
                            }
                            ListView ratesListView = view.findViewById(R.id.rates_list_view);
                            ratesListView.setAdapter(adapter);
                            successfulRequest();
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

    @SuppressLint("ResourceType")
    private void setExtraInfo(CurrencyInformation currency) {
        Resources res = context.getResources();
        try {
            TypedArray extraInfo = res.obtainTypedArray(res
                    .getIdentifier(currency.getCode(), "array", context.getPackageName()));
            currency.setName(extraInfo.getString(0));
            currency.setIcon(extraInfo.getResourceId(1, 1));
        } catch (Resources.NotFoundException e) {
            currency.setName("Unknown");
            currency.setIcon(R.drawable.flag_unknown);
        }
    }

    public void getRefreshedExchangeResult() {
        if (!isInternetConnected()) {
            noInternetError();
            return;
        }

        TextView fromCode = view.findViewById(R.id.from_currency_code);
        TextView toCode = view.findViewById(R.id.to_currency_code);
        String initialCurrency = fromCode.getText().toString();
        String targetCurrency = toCode.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET,
                //CurrencyAPI.getExchangeLink(initialCurrency, targetCurrency),
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

                            CurrencyInformation currency = new CurrencyInformation(initialCurrency, null);
                            setExtraInfo(currency);
                            TextView fromName = view.findViewById(R.id.from_currency_name);
                            ImageView fromIcon = view.findViewById(R.id.from_currency_icon);
                            fromName.setText(currency.getName());
                            fromIcon.setImageResource(currency.getIcon());

                            CurrencyInformation currency2 = new CurrencyInformation(targetCurrency, null);
                            setExtraInfo(currency2);
                            TextView toName = view.findViewById(R.id.to_currency_name);
                            ImageView toIcon = view.findViewById(R.id.to_currency_icon);
                            toName.setText(currency2.getName());
                            toIcon.setImageResource(currency2.getIcon());

                            TextView exchangeAmount = view.findViewById(R.id.from_currency_value);
                            TextView exchangeResult = view.findViewById(R.id.to_currency_value);
                            exchangeResult.setText(MathOperations.calculateExchangeResult(context,
                                    exchangeAmount.getText().toString(), currencyRate.toString()));
                            successfulRequest();
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
        showToast(NO_INTERNET_ERROR);
    }

    private void serverSideError() {
        View viewToShow = view.findViewById(R.id.server_side_error_view);
        viewToShow.setVisibility(View.VISIBLE);
        viewToShow.bringToFront();
        showToast(SERVER_SIDE_ERROR);
    }

    private void showToast(int responseStatus) {
        StringBuilder message = new StringBuilder();
        if (responseStatus == NO_INTERNET_ERROR) {
            message.append("No Internet connection!");
        } else if (responseStatus == SERVER_SIDE_ERROR) {
            message.append("Oops! Couldn't receive data!");
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}


