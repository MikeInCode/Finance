package mike.finance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

    private Context context;
    private LayoutInflater inflater;
    private List<CurrencyInformation> currencyList;
    private List<CurrencyInformation> list;
    private SharedPreferences preferences;

    public DialogListAdapter(Context context, List<CurrencyInformation> currencyList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.currencyList = currencyList;
        list = new ArrayList<>(currencyList);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        if (preferences.getBoolean(currencyListItem.getCode() + context
                .getString(R.string.is_favorite), false)) {
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

    public void filter(String text) {
        text = text.toLowerCase();
        currencyList.clear();
        if (text.length() == 0) {
            currencyList.addAll(list);
        } else {
            for (CurrencyInformation i : list) {
                if (i.getCode().toLowerCase().contains(text) || i.getName().toLowerCase().contains(text)) {
                    CurrencyInformation currency = new CurrencyInformation(i.getCode(), i.getRate());
                    currency.setName(i.getName());
                    currency.setIcon(i.getIcon());
                    currencyList.add(currency);
                }
            }
        }
        notifyDataSetChanged();
    }
}

