package informante.dto;

import java.util.Date;
import java.util.List;

public class IPInformationResponse {

    private String ip;
    private Date currentDate;
    private String country;
    private List<String> isoCodes;
    private List<String> languages;
    private List<String> currencies;
    private List<Long> currenciesRatesInUSD;
    private List<String> timeZones;
    private double estimatedDistance;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getIsoCodes() {
        return isoCodes;
    }

    public void setIsoCodes(List<String> isoCodes) {
        this.isoCodes = isoCodes;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }

    public List<Long> getCurrenciesRatesInUSD() {
        return currenciesRatesInUSD;
    }

    public void setCurrenciesRatesInUSD(List<Long> currenciesRatesInUSD) {
        this.currenciesRatesInUSD = currenciesRatesInUSD;
    }

    public List<String> getTimeZones() {
        return timeZones;
    }

    public void setTimeZones(List<String> timeZones) {
        this.timeZones = timeZones;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(double estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }
}
