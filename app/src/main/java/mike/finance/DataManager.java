package mike.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import mike.finance.rates.RatesAdapter;

public final class DataManager implements Serializable, Comparator<CurrencyInformation> {

    @Nullable @BindView(R.id.rates_list_view) ListView ratesListView;
    @Nullable @BindView(R.id.initial_currency_code) TextView initialCode;
    @Nullable @BindView(R.id.initial_currency_name) TextView initialName;
    @Nullable @BindView(R.id.initial_currency_icon) ImageView initialIcon;
    @Nullable @BindView(R.id.target_currency_code) TextView targetCode;
    @Nullable @BindView(R.id.target_currency_name) TextView targetName;
    @Nullable @BindView(R.id.target_currency_icon) ImageView targetIcon;
    @Nullable @BindView(R.id.exchange_amount) TextView exchangeAmount;
    @Nullable @BindView(R.id.exchange_result) TextView exchangeResult;

    @Nullable @BindView(R.id.no_internet_view) View noInternetView;
    @Nullable @BindView(R.id.server_side_error_view) View serverSideErrorView;
    @Nullable @BindView(R.id.empty_favorites_view) View emptyFavoritesView;

    @BindString(R.string.show_favorites) String showFavoritesKey;
    @BindString(R.string.is_favorite) String isFavorite;
    @BindString(R.string.sorting_type) String sortingTypeKey;
    @BindString(R.string.sorting_direction) String sortingDirectionKey;

    private View view;
    private Context context;
    private List<CurrencyInformation> currencyList;
    private RatesAdapter adapter;
    private SharedPreferences preferences;

    public DataManager(View view) {
        this.view = view;
        context = view.getContext();
        currencyList = new ArrayList<>();
        adapter = new RatesAdapter(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public RatesAdapter getAdapter() {
        return adapter;
    }

    public List<CurrencyInformation> getDialogCurrencyList() {
        List<CurrencyInformation> dialogCurrencyList = new ArrayList<>();
        for (CurrencyInformation i : currencyList) {
            if (preferences.getBoolean(i.getCode() + isFavorite, false)) {
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

    public void getRefreshedRates() {
        ButterKnife.bind(this, view);
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

                            rebuildCurrencyListIfItChanged(mapper, jsonChild);

                            for (CurrencyInformation i : currencyList) {
                                i.setRate(jsonChild.get(i.getCode()).toString());
                            }

                            buildRatesListView();
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

    public void getRefreshedExchangeResult() {
        ButterKnife.bind(this, view);
        if (!isInternetConnected()) {
            noInternetError();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET,
                //CurrencyAPI.getExchangeLink(initialCode, targetCode),
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

                            setExchangeCurrenciesInfo();

                            exchangeResult.setText(calculateExchangeResult(exchangeAmount
                                    .getText().toString(), currencyRate.toString()));
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
    private void setCurrencyExtraInfo(CurrencyInformation currency) {
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

    private void rebuildCurrencyListIfItChanged(ObjectMapper mapper, JsonNode jsonNode) {
        if (currencyList.size() != jsonNode.size()) {
            currencyList.clear();
            try {
                Map<String, String> map = mapper.readValue(jsonNode.toString(), new TypeReference<TreeMap<String, String>>() {
                });
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    // key - currency code; value - currency rate
                    String key = entry.getKey();
                    String value = entry.getValue();
                    CurrencyInformation currency = new CurrencyInformation(key, value);
                    setCurrencyExtraInfo(currency);
                    currencyList.add(currency);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildRatesListView() {
        adapter.setCurrencyList(generateCurrencyList(currencyList));
        ratesListView.setAdapter(adapter);
    }

    private void setExchangeCurrenciesInfo() {
        CurrencyInformation currency = new CurrencyInformation(initialCode
                .getText().toString(), null);
        setCurrencyExtraInfo(currency);
        initialName.setText(currency.getName());
        initialIcon.setImageResource(currency.getIcon());

        CurrencyInformation currency2 = new CurrencyInformation(targetCode
                .getText().toString(), null);
        setCurrencyExtraInfo(currency2);
        targetName.setText(currency2.getName());
        targetIcon.setImageResource(currency2.getIcon());
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void successfulRequest() {
        noInternetView.setVisibility(View.INVISIBLE);
        serverSideErrorView.setVisibility(View.INVISIBLE);
    }

    private void noInternetError() {
        noInternetView.setVisibility(View.VISIBLE);
        noInternetView.bringToFront();
    }

    private void serverSideError() {
        serverSideErrorView.setVisibility(View.VISIBLE);
        serverSideErrorView.bringToFront();
    }

    private void emptyFavoritesErrorActive() {
        emptyFavoritesView.setVisibility(View.VISIBLE);
        emptyFavoritesView.bringToFront();
    }

    private void emptyFavoritesErrorInactive() {
        emptyFavoritesView.setVisibility(View.INVISIBLE);
        emptyFavoritesView.bringToFront();
    }

    private List<CurrencyInformation> generateCurrencyList(List<CurrencyInformation> list) {
        List<CurrencyInformation> finalCurrencyList = new ArrayList<>();
        if (preferences.getBoolean(showFavoritesKey, false)) {
            List<CurrencyInformation> favoriteCurrencyList = new ArrayList<>();
            for (CurrencyInformation i : list) {
                if (preferences.getBoolean(i.getCode() + isFavorite, false)) {
                    favoriteCurrencyList.add(i);
                }
            }
            if (!favoriteCurrencyList.isEmpty()) {
                finalCurrencyList = new ArrayList<>(favoriteCurrencyList);
                Collections.sort(finalCurrencyList, this);
            } else {
                emptyFavoritesErrorActive();
            }
        } else {
            finalCurrencyList = new ArrayList<>(list);
            Collections.sort(finalCurrencyList, this);
            emptyFavoritesErrorInactive();
        }

        return finalCurrencyList;
    }

    @Override
    public int compare(CurrencyInformation o1, CurrencyInformation o2) {
        boolean sortingType = preferences.getBoolean(sortingTypeKey, true);
        boolean sortingDirection = preferences.getBoolean(sortingDirectionKey, true);
        if (sortingType) {
            if (sortingDirection) {
                return o1.getCode().compareTo(o2.getCode());
            } else {
                return o2.getCode().compareTo(o1.getCode());
            }
        } else {
            if (sortingDirection) {
                return Double.valueOf(o1.getRate()).compareTo(Double.valueOf(o2.getRate()));
            } else {
                return Double.valueOf(o2.getRate()).compareTo(Double.valueOf(o1.getRate()));
            }
        }
    }

    private String calculateExchangeResult(String amount, String rate) {
        try {
            int digits = Integer.parseInt(preferences.getString(context.getString(R.string.number_precision), "3"));
            return new BigDecimal(amount).multiply(new BigDecimal(rate))
                    .setScale(digits, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}


