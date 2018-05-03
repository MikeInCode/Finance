package mike.finance;

public class CurrencyInformation {
    private String code;
    private String name;
    private int icon;
    private String rate;

    public CurrencyInformation(String code, String rate) {
        this.code = code;
        this.rate = rate;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public String getRate() {
        return rate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
