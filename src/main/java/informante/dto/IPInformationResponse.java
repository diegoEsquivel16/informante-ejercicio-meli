package informante.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class IPInformationResponse {

    private String ip;
    private Date currentDate;
    private String country;
    private List<String> isoCodes;
    private List<String> languages;
    private List<String> currencies;
    private Map<String, Long> currenciesRatesInUSD;
    private List<String> timeZones;
    private List<Map<String,String>> datesWithTimeZoneMap;
    private double estimatedDistance;
    private List<List<Double>> coordinates;

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

    public Map<String, Long> getCurrenciesRatesInUSD() {
        return currenciesRatesInUSD;
    }

    public void setCurrenciesRatesInUSD(Map<String, Long> currenciesRatesInUSD) {
        this.currenciesRatesInUSD = currenciesRatesInUSD;
    }

    public List<String> getTimeZones() {
        return timeZones;
    }

    public void setTimeZones(List<String> timeZones) {
        this.timeZones = timeZones;
    }

    public List<Map<String, String>> getDatesWithTimeZoneMap() {
        return datesWithTimeZoneMap;
    }

    public void setDatesWithTimeZoneMap(List<Map<String, String>> datesWithTimeZoneMap) {
        this.datesWithTimeZoneMap = datesWithTimeZoneMap;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(double estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }
}
