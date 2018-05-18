package mike.finance.rates;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mike.finance.CurrencyInformation;
import mike.finance.R;

public class RatesAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<CurrencyInformation> currencyList;
    private List<CurrencyInformation> searchingCurrencyList;
    private SharedPreferences preferences;

    public RatesAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setCurrencyList(List<CurrencyInformation> currencyList) {
        this.currencyList = currencyList;
        searchingCurrencyList = new ArrayList<>(currencyList);
    }

    @Override
    public int getCount() {
        return currencyList.size();
    }

    @Override
    public Object getItem(int position) {
        return currencyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.rates_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CurrencyInformation currencyListItem = (CurrencyInformation) getItem(position);
        viewHolder.currencyCode.setText(currencyListItem.getCode());
        viewHolder.currencyName.setText(currencyListItem.getName());
        viewHolder.currencyIcon.setImageResource(currencyListItem.getIcon());
        viewHolder.currencyRate.setText(setRightRate(currencyListItem.getRate()));

        String key = currencyListItem.getCode() + context.getString(R.string.is_favorite);
        if (!preferences.getBoolean(key, false)) {
            viewHolder.isFavorite.setImageResource(R.drawable.ic_favorite_inactive);
        } else {
            viewHolder.isFavorite.setImageResource(R.drawable.ic_favorite_active);
        }

        viewHolder.isFavorite.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                if (!preferences.getBoolean(key, false)) {
                    editor.putBoolean(key, true);
                    editor.apply();
                    viewHolder.isFavorite.setImageResource(R.drawable.ic_favorite_active);
                } else {
                    editor.putBoolean(key, false);
                    editor.apply();
                    viewHolder.isFavorite.setImageResource(R.drawable.ic_favorite_inactive);
                }
                return false;
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.currency_code) TextView currencyCode;
        @BindView(R.id.currency_name) TextView currencyName;
        @BindView(R.id.currency_icon) ImageView currencyIcon;
        @BindView(R.id.currency_rate) TextView currencyRate;
        @BindView(R.id.favorite_button) ImageButton isFavorite;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public boolean filter(String text) {
        text = text.toLowerCase();
        currencyList.clear();
        if (text.length() == 0) {
            currencyList.addAll(searchingCurrencyList);
        } else {
            for (CurrencyInformation i : searchingCurrencyList) {
                if (i.getCode().toLowerCase().contains(text) || i.getName().toLowerCase().contains(text)) {
                    CurrencyInformation currency = new CurrencyInformation(i.getCode(), i.getRate());
                    currency.setName(i.getName());
                    currency.setIcon(i.getIcon());
                    currencyList.add(currency);
                }
            }
        }
        notifyDataSetChanged();
        return !currencyList.isEmpty();
    }

    private String setRightRate(String rate) {
        try {
            int digits = Integer.parseInt(preferences.getString(context.getString(R.string.number_precision), "3"));
            return new BigDecimal(1).divide(new BigDecimal(rate), digits, BigDecimal.ROUND_HALF_UP)
                    .stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}