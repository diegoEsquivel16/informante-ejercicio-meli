package informante.service;

import informante.connector.CountriesConnector;
import informante.connector.CurrenciesConnector;
import informante.connector.GeoConnector;
import informante.dto.CountryInformation;
import informante.dto.IPGeoLocation;
import informante.dto.IPInformationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InformanteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InformanteService.class);
    private GeoConnector geoConnector;
    private CountriesConnector countriesConnector;
    private CurrenciesConnector currenciesConnector;
    private double pointReferenceLatitude;//BUE LAT -34.6131516
    private double pointReferenceLongitude;//BUE LONG -58.3772316


    public InformanteService(GeoConnector geoConnector, CountriesConnector countriesConnector, CurrenciesConnector currenciesConnector, double pointReferenceLatitude, double pointReferenceLongitude) {
        this.geoConnector = geoConnector;
        this.countriesConnector = countriesConnector;
        this.currenciesConnector = currenciesConnector;
        this.pointReferenceLatitude = pointReferenceLatitude;
        this.pointReferenceLongitude = pointReferenceLongitude;
    }

    public IPInformationResponse getIpInformation(String ip){
        IPGeoLocation ipGeoLocation = getGeoLocation(ip);
        CountryInformation countryInformation = getCountryInformation(ipGeoLocation);
        List<Long> currenciesRates = getCurrenciesInformationRates(countryInformation);
        return buildIPInformationResponse(ip, ipGeoLocation, countryInformation, currenciesRates);
    }

    private IPGeoLocation getGeoLocation(String ip){
        try {
            return geoConnector.getIPGeoLocation(ip);
        } catch (Exception e){
            LOGGER.error("Couldn't found the geo location for the ip {}", ip);
            throw new SecurityException();
        }
    }

    private List<Long> getCurrenciesInformationRates(CountryInformation countryInformation) {
        return new ArrayList<>(currenciesConnector.getCurrencies().getRates().values());
    }

    private CountryInformation getCountryInformation(IPGeoLocation ipGeoLocation) {
        return countriesConnector.getCountryInformation(ipGeoLocation.getCountryCode());
    }

    private IPInformationResponse buildIPInformationResponse(String ip, IPGeoLocation ipGeoLocation, CountryInformation countryInformation, List<Long> currenciesRates) {
        IPInformationResponse response = new IPInformationResponse();
        response.setIp(ip);
        response.setCurrentDate(new Date());
        response.setCountry(ipGeoLocation.getCountryName());
        //response.setIsoCodes(countryInformation.getiso());
        //response.setLanguages(countryInformation.getLanguages);
        //response.setCurrency(countryInformation.getCurrency);

        return null;
    }

}
