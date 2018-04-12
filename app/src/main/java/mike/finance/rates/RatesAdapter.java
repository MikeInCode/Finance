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

import java.util.List;

import mike.finance.CurrencyInformation;
import mike.finance.MathOperations;
import mike.finance.R;

public class RatesAdapter extends BaseAdapter {

    private Context context;
    private List<CurrencyInformation> currencyList;
    private SharedPreferences prefs;

    /**
     * pattern "ViewHolder" for the optimize usage of device's resources
     */
    private static class ViewHolder {
        TextView currencyCode;
        TextView currencyName;
        ImageView currencyIcon;
        TextView currencyRate;
        ImageButton isFavourite;
    }

    public RatesAdapter(Context context, List<CurrencyInformation> currencyList) {
        this.context = context;
        this.currencyList = currencyList;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
            viewHolder.currencyCode = convertView.findViewById(R.id.currency_abbreviation);
            viewHolder.currencyName = convertView.findViewById(R.id.currency_full_name);
            viewHolder.currencyRate = convertView.findViewById(R.id.currency_rate);
            viewHolder.isFavourite = convertView.findViewById(R.id.favourite_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.currencyIcon.setImageResource(context.getResources()
                .getIdentifier(currencyListItem.getIcon(), "drawable", context.getPackageName()));
        viewHolder.currencyCode.setText(currencyListItem.getCode());
        viewHolder.currencyName.setText(currencyListItem.getName());
        viewHolder.currencyRate.setText(MathOperations.setRightRate(prefs, currencyListItem.getRate()));

        if (!prefs.getBoolean(currencyListItem.getCode() + "_favorite", false)) {
            viewHolder.isFavourite.setImageResource(R.drawable.ic_favorite_inactive);
        } else {
            viewHolder.isFavourite.setImageResource(R.drawable.ic_favorite_active);
        }

        viewHolder.isFavourite.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                if (!prefs.getBoolean(currencyListItem.getCode() + "_favorite", false)) {
                    viewHolder.isFavourite.setImageResource(R.drawable.ic_favorite_active);
                    editor.putBoolean(currencyListItem.getCode() + "_favorite", true);
                    editor.apply();
                } else {
                    viewHolder.isFavourite.setImageResource(R.drawable.ic_favorite_inactive);
                    editor.putBoolean(currencyListItem.getCode() + "_favorite", false);
                    editor.apply();
                }
                return false;
            }
        });

        return convertView;
    }
}

