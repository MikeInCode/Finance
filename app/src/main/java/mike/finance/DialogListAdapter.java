package mike.finance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<CurrencyInformation> currencyList;
    private List<CurrencyInformation> searchingCurrencyList;
    private SharedPreferencesAccessor prefs;

    public DialogListAdapter(Context context, List<CurrencyInformation> currencyList) {
        inflater = LayoutInflater.from(context);
        this.currencyList = currencyList;
        searchingCurrencyList = new ArrayList<>(currencyList);
        prefs = new SharedPreferencesAccessor(context);
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
            convertView = inflater.inflate(R.layout.dialog_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CurrencyInformation currencyListItem = (CurrencyInformation) getItem(position);
        viewHolder.currencyCode.setText(currencyListItem.getCode());
        viewHolder.currencyName.setText(currencyListItem.getName());
        if (prefs.isFavorite(currencyListItem.getCode())) {
            viewHolder.favIcon.setImageResource(R.drawable.ic_favorite_active);
        } else {
            viewHolder.favIcon.setImageDrawable(null);
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.dialog_currency_code) TextView currencyCode;
        @BindView(R.id.dialog_currency_name) TextView currencyName;
        @BindView(R.id.dialog_fav_icon) ImageView favIcon;

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
}

