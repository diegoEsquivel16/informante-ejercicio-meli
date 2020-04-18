package informante.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CountryInformation {

    private String name;
    @JsonProperty("latlng")
    private List<Integer> latitudeAndLongitude;
    private List<String> timezones;
    private List<CurrencyInformation> currencies;
    private List<LanguageInformation> languages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getLatitudeAndLongitude() {
        return latitudeAndLongitude;
    }

    public void setLatitudeAndLongitude(List<Integer> latitudeAndLongitude) {
        this.latitudeAndLongitude = latitudeAndLongitude;
    }

    public Integer getLatitude(){
        return getLatitudeAndLongitude().get(0);
    }

    public Integer getLongitude(){
        return getLatitudeAndLongitude().get(1);
    }

    public List<String> getTimezones() {
        return timezones;
    }

    public void setTimezones(List<String> timezones) {
        this.timezones = timezones;
    }

    public List<CurrencyInformation> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CurrencyInformation> currencies) {
        this.currencies = currencies;
    }

    public List<LanguageInformation> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageInformation> languages) {
        this.languages = languages;
    }
}
