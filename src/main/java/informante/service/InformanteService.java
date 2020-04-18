package informante.service;

import informante.connector.CountriesConnector;
import informante.connector.CurrenciesConnector;
import informante.connector.GeoConnector;
import informante.dto.*;
import informante.utils.GeoDistanceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class InformanteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InformanteService.class);
    private final GeoConnector geoConnector;
    private final CountriesConnector countriesConnector;
    private final CurrenciesConnector currenciesConnector;
    private double POINT_REFERENCE_LATITUDE;//BUE LAT -34.6131516
    private final double POINT_REFERENCE_LONGITUDE;//BUE LONG -58.3772316
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");

    public InformanteService(GeoConnector geoConnector, CountriesConnector countriesConnector, CurrenciesConnector currenciesConnector, double POINT_REFERENCE_LATITUDE, double POINT_REFERENCE_LONGITUDE) {
        this.geoConnector = geoConnector;
        this.countriesConnector = countriesConnector;
        this.currenciesConnector = currenciesConnector;
        this.POINT_REFERENCE_LATITUDE = POINT_REFERENCE_LATITUDE;
        this.POINT_REFERENCE_LONGITUDE = POINT_REFERENCE_LONGITUDE;
    }

    public IPInformationResponse getIpInformation(String ip){
        IPGeoLocation ipGeoLocation = getGeoLocation(ip);
        CountryInformation countryInformation = getCountryInformation(ipGeoLocation);
        Map<String, Long> currenciesRates = getCurrenciesInformationRates(countryInformation);
        return buildIPInformationResponse(ip, ipGeoLocation, countryInformation, currenciesRates);
    }

    private IPGeoLocation getGeoLocation(String ip){
        return geoConnector.getIPGeoLocation(ip);//TODO si queda esto solo lo pongo arriba
    }

    private Map<String, Long> getCurrenciesInformationRates(CountryInformation countryInformation) {
        List<String> currencyCodes = countryInformation.getCurrencies().stream().map(CurrencyInformation::getCode).collect(Collectors.toList());
        try{
            CurrencyServiceResponse currencyRates = currenciesConnector.getCurrenciesWithUSDAsBase(currencyCodes);
            return currencyRates.getRates();

        } catch (Exception exc){
            LOGGER.error("Couldn't find the currencies {} of the country {}", currencyCodes, countryInformation.getName());
            return new HashMap<>();
        }
    }

    private CountryInformation getCountryInformation(IPGeoLocation ipGeoLocation) {
        CountryInformation countryInformation;
        try{
            countryInformation = countriesConnector.getCountryInformation(ipGeoLocation.getCountryCode3());
        } catch (Exception exc){
            LOGGER.error("Couldn't find the country information of {} with code {}. Retrying with other county code", ipGeoLocation.getCountryName(), ipGeoLocation.getCountryCode());
            countryInformation = countriesConnector.getCountryInformation(ipGeoLocation.getCountryCode3());
        }
        return countryInformation;
    }

    private IPInformationResponse buildIPInformationResponse(String ip, IPGeoLocation ipGeoLocation, CountryInformation countryInformation, Map<String, Long> currenciesRates) {
        IPInformationResponse response = new IPInformationResponse();
        Date now = new Date();
        response.setIp(ip);
        response.setCurrentDate(now);
        response.setCountry(ipGeoLocation.getCountryName());
        response.setIsoCodes(getIsoCodes(countryInformation));
        response.setLanguages(getLanguages(countryInformation));
        response.setCurrencies(getCurrencyCodes(countryInformation));
        response.setCurrenciesRatesInUSD(currenciesRates);
        response.setTimeZones(countryInformation.getTimezones());
        response.setDatesWithTimeZoneMap(buildDatesWithTimeZoneMap(now, countryInformation));
        response.setEstimatedDistance(calculateDistanceFromReferencePoint(countryInformation));
        response.setCoordinates(buildCoordinates(countryInformation));

        return null;
    }

    private List<List<Double>> buildCoordinates(CountryInformation countryInformation) {
        return Arrays.asList(Arrays.asList(POINT_REFERENCE_LATITUDE, POINT_REFERENCE_LONGITUDE),
                Arrays.asList(countryInformation.getLatitude().doubleValue(), countryInformation.getLongitude().doubleValue()));
    }

    private List<String> getIsoCodes(CountryInformation countryInformation){
        return countryInformation.getLanguages().stream().map(LanguageInformation::getIso639One).collect(Collectors.toList());
    }

    private List<String> getLanguages(CountryInformation countryInformation){
        return countryInformation.getLanguages().stream().map(LanguageInformation::getName).collect(Collectors.toList());
    }

    private List<String> getCurrencyCodes(CountryInformation countryInformation){
        return countryInformation.getCurrencies().stream().map(CurrencyInformation::getCode).collect(Collectors.toList());
    }

    private List<Map<String,String>> buildDatesWithTimeZoneMap(Date now, CountryInformation countryInformation){
        return countryInformation.getTimezones().stream()
                .map(t-> buildDateMap(now, t)).collect(Collectors.toList());
    }

    private Map<String,String> buildDateMap(Date now, String timezone){
        ZoneOffset zoneOffset = ZoneOffset.of(timezone.replace("UTC", ""));
        String formattedDate = now.toInstant().atOffset(zoneOffset).format(dateFormatter);
        return Map.ofEntries(Map.entry(timezone, formattedDate));
    }

    private double calculateDistanceFromReferencePoint(CountryInformation countryInformation){
        return GeoDistanceCalculator.getDistanceInKM(POINT_REFERENCE_LATITUDE, countryInformation.getLatitude(),
                POINT_REFERENCE_LONGITUDE, countryInformation.getLongitude());
    }

    public static void main(String[] args) {//TODO BORRAR ESTO SI ANDA
        String HOUR_FORMAT = "dd/MM/yy HH:mm:ss";
        OffsetDateTime datee = new Date().toInstant().atOffset(ZoneOffset.of("-08:00"));
        System.out.println("Hora UTC-8:00: "+ datee.format(DateTimeFormatter.ofPattern(HOUR_FORMAT)));
    }
}
