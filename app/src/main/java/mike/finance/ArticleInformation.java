package mike.finance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@JsonIgnoreProperties({"source", "author"})
public class ArticleInformation {

    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description.length() > 80) {
            description = description.substring(0, 80);
            description += "...";
        }
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        DateTime responseDate = new DateTime(publishedAt, DateTimeZone.UTC);
        DateTimeFormatter format = DateTimeFormat.forPattern("MMM dd, yyyy HH:mm");
        this.publishedAt = responseDate.withZone(DateTimeZone.getDefault()).toString(format);
    }
}
