package mike.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class DataManager implements Serializable, Comparator<CurrencyInformation> {

    //News fragment
    @Nullable @BindView(R.id.news_list_view) ListView newsListView;

    private Context context;
    private List<CurrencyInformation> currencyList;
    private List<ArticleInformation> newsList;
    private SharedPreferencesAccessor prefs;

    public DataManager() {
        currencyList = new ArrayList<>();
    }

    public void init(View view) {
        context = view.getContext();
        prefs = new SharedPreferencesAccessor(context);
        ButterKnife.bind(this, view);
    }

    public List<CurrencyInformation> getDialogCurrencyList() {
        List<CurrencyInformation> dialogCurrencyList = new ArrayList<>();
        for (CurrencyInformation i : currencyList) {
            if (prefs.isFavorite(i.getCode())) {
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

    private interface BaseCallback {
        void onNoInternetError();

        void onServerSideError();
    }

    public interface RatesCallback extends BaseCallback {
        void onSuccessfulRequest(List<CurrencyInformation> list);
    }

    public interface ExchangeCallback extends BaseCallback {
        void onSuccessfulRequest(CurrencyInformation initialCurrency, CurrencyInformation targetCurrency);
    }

    public interface NewsCallback extends BaseCallback {
        void onSuccessfulRequest(List<ArticleInformation> list);
    }

    public void getRatesData(RatesCallback ratesCallback) {
        if (!isInternetConnected()) {
            ratesCallback.onNoInternetError();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET,
                new APIManager(context).getRatesLink(prefs.getBaseCurrencyValue()),
                new Response.Listener<String>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onResponse(String response) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode currencyJsonArray = mapper.readTree(response).get("rates");
                            if (currencyJsonArray == null) {
                                ratesCallback.onServerSideError();
                                return;
                            }

                            rebuildCurrencyListIfItChanged(mapper, currencyJsonArray);
                            for (CurrencyInformation i : currencyList) {
                                i.setRate(currencyJsonArray.get(i.getCode()).toString());
                            }
                            if (prefs.getShowFavoritesValue()) {
                                ratesCallback.onSuccessfulRequest(getSortedCurrencyList(generateFavoriteCurrencyList()));
                            } else {
                                ratesCallback.onSuccessfulRequest(getSortedCurrencyList(currencyList));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ratesCallback.onServerSideError();
                    }
                }));

    }

    public void getExchangeData(String initialCode, String targetCode, String amount, ExchangeCallback exchangeCallback) {
        if (!isInternetConnected()) {
            exchangeCallback.onNoInternetError();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET,
                new APIManager(context).getExchangeLink(initialCode, targetCode),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode currencyRate = mapper.readTree(response).get("rate");
                            if (currencyRate == null) {
                                exchangeCallback.onServerSideError();
                                return;
                            }

                            CurrencyInformation initialCurrency = new CurrencyInformation(initialCode, amount);
                            setCurrencyExtraInfo(initialCurrency);
                            CurrencyInformation targetCurrency = new CurrencyInformation(targetCode,
                                    calculateExchangeResult(amount, currencyRate.toString()));
                            setCurrencyExtraInfo(targetCurrency);

                            exchangeCallback.onSuccessfulRequest(initialCurrency, targetCurrency);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        exchangeCallback.onServerSideError();
                    }
                }));
    }

    public void getNewsData(NewsCallback newsCallback) {
        if (!isInternetConnected()) {
            newsCallback.onNoInternetError();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(new StringRequest(Request.Method.GET,
                new APIManager(context).getNewsLink(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode newsJsonArray = mapper.readTree(response).get("articles");
                            if (newsJsonArray == null) {
                                newsCallback.onServerSideError();
                                return;
                            }

                            newsList = Arrays.asList(mapper.readValue(newsJsonArray.toString(), ArticleInformation[].class));
                            newsCallback.onSuccessfulRequest(newsList);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        newsCallback.onServerSideError();
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

    private boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private List<CurrencyInformation> generateFavoriteCurrencyList() {
        List<CurrencyInformation> favoriteCurrencyList = new ArrayList<>();
        for (CurrencyInformation i : currencyList) {
            if (prefs.isFavorite(i.getCode())) {
                favoriteCurrencyList.add(i);
            }
        }
        return favoriteCurrencyList;
    }

    private List<CurrencyInformation> getSortedCurrencyList(List<CurrencyInformation> listToSort) {
        List<CurrencyInformation> sortedList = new ArrayList<>(listToSort);
        Collections.sort(sortedList, this);
        return sortedList;
    }

    @Override
    public int compare(CurrencyInformation o1, CurrencyInformation o2) {
        int sortingType = prefs.getRatesSortingTypeValue();
        if (sortingType == 0) {
            return o1.getCode().compareTo(o2.getCode());
        } else if (sortingType == 1) {
            return o2.getCode().compareTo(o1.getCode());
        } else if (sortingType == 2) {
            return Double.valueOf(o2.getRate()).compareTo(Double.valueOf(o1.getRate()));
        } else if (sortingType == 3) {
            return Double.valueOf(o1.getRate()).compareTo(Double.valueOf(o2.getRate()));
        } else {
            return o1.getCode().compareTo(o2.getCode());
        }
    }

    private String calculateExchangeResult(String amount, String rate) {
        try {
            int digits = Integer.parseInt(prefs.getNumberPrecisionValue());
            return new BigDecimal(amount).multiply(new BigDecimal(rate))
                    .setScale(digits, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}