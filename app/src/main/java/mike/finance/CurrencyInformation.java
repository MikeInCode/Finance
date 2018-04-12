package mike.finance;

public class CurrencyInformation {
    private String code;
    private String name;
    private String icon;
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

    public String getIcon() {
        return icon;
    }

    public String getRate() {
        return rate;
    }

    public CurrencyInformation setName(String name) {
        this.name = name;
        return this;
    }

    public CurrencyInformation setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
