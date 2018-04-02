package mike.finance.rates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mike.finance.CurrencyInformation;
import mike.finance.MathOperations;
import mike.finance.R;

public class RatesAdapter extends BaseAdapter {

    private Context context;
    private List<CurrencyInformation> currencyList;
    private String primaryCurrency;

    /**
     * pattern "ViewHolder" for the optimize usage of device's resources
     */
    private static class ViewHolder {
        TextView currencyAbbreviation;
        TextView currencyFullName;
        ImageView currencyIcon;
        TextView currencyRate;
        //ImageButton isFavouriteButton;
    }

    public RatesAdapter(Context context) {
        this.context = context;
    }

    public RatesAdapter setCurrencyList(List<CurrencyInformation> currencyList) {
        this.currencyList = currencyList;
        return this;
    }

    public RatesAdapter setPrimaryCurrency(String primaryCurrency) {
        this.primaryCurrency = primaryCurrency;
        return this;
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

    /**
     * Fill the each line of ListView by the data
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CurrencyInformation currencyListItem = (CurrencyInformation) getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.rates_list_item, parent, false);
            viewHolder.currencyIcon = convertView.findViewById(R.id.currency_icon);
            viewHolder.currencyAbbreviation = convertView.findViewById(R.id.currency_abbreviation);
            viewHolder.currencyFullName = convertView.findViewById(R.id.currency_full_name);
            viewHolder.currencyRate = convertView.findViewById(R.id.currency_rate);
            //viewHolder.isFavouriteButton = convertView.findViewById(R.id.favourite_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (!currencyListItem.getCurrencyAbbreviation().equals(primaryCurrency)) {
            viewHolder.currencyIcon.setImageResource(context.getResources()
                    .getIdentifier(currencyListItem.getCurrencyIcon(), "drawable", context.getPackageName()));
            viewHolder.currencyAbbreviation.setText(currencyListItem.getCurrencyAbbreviation());
            viewHolder.currencyFullName.setText(currencyListItem.getCurrencyFullName());
            viewHolder.currencyRate.setText(MathOperations.round(currencyListItem.getCurrencyRate()));
        }

        //viewHolder.isFavouriteButton.setImageResource(R.drawable.ic_fav_button_on);
        return convertView;
    }
}

