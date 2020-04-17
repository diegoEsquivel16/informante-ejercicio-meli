package informante.dto;

import java.util.Date;
import java.util.Map;

public class CurrencyServiceResponse {

    private Map<String, Long> rates;
    private String base;
    private Date date;

    public Map<String, Long> getRates() {
        return rates;
    }

    public void setRates(Map<String, Long> rates) {
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
