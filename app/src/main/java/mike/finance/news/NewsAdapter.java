package mike.finance.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import mike.finance.ArticleInformation;
import mike.finance.R;

public class NewsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<ArticleInformation> newsList;
    private NewsFragment.NewsAdapterCallback callback;

    public NewsAdapter(Context context, NewsFragment.NewsAdapterCallback callback) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.callback = callback;
    }

    public void setNewsList(List<ArticleInformation> newsList) {
        this.newsList = newsList;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.news_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArticleInformation newsListItem = (ArticleInformation) getItem(position);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_picture)
                .error(R.drawable.ic_picture);
        Glide.with(context).load(newsListItem.getUrlToImage())
                .apply(options)
                .into(viewHolder.newsImage);

        viewHolder.newsTitle.setText(newsListItem.getTitle());
        viewHolder.newsDescription.setText(newsListItem.getDescription());
        viewHolder.publishedDate.setText(newsListItem.getPublishedAt());
        viewHolder.readMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onReadButtonClicked(view, position, newsListItem.getUrl());
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.news_image) CircleImageView newsImage;
        @BindView(R.id.news_title) TextView newsTitle;
        @BindView(R.id.news_description) TextView newsDescription;
        @BindView(R.id.published_date) TextView publishedDate;
        @BindView(R.id.read_more_button) Button readMoreBtn;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}