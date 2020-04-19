package informante.service;

import informante.connector.CountriesConnector;
import informante.connector.CurrenciesConnector;
import informante.connector.GeoConnector;
import informante.dto.*;
import informante.repository.CountryInformationRepository;
import informante.repository.InvocationsPerCountryHistoryRepository;
import informante.utils.GeoDistanceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InformanteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InformanteService.class);
    private final GeoConnector geoConnector;
    private final CountriesConnector countriesConnector;
    private final CurrenciesConnector currenciesConnector;
    private final InvocationsPerCountryHistoryRepository invocationsRepository;
    private final CountryInformationRepository countryInformationRepository;
    private final double POINT_REFERENCE_LATITUDE;
    private final double POINT_REFERENCE_LONGITUDE;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");

    @Autowired
    public InformanteService(GeoConnector geoConnector, CountriesConnector countriesConnector, CurrenciesConnector currenciesConnector,
                             InvocationsPerCountryHistoryRepository invocationsRepository, CountryInformationRepository countryInformationRepository, @Value("${point-reference-latitude}") double pointReferenceLatitude, @Value("${point-reference-longitude}") double pointReferenceLongitude) {
        this.geoConnector = geoConnector;
        this.countriesConnector = countriesConnector;
        this.currenciesConnector = currenciesConnector;
        this.invocationsRepository = invocationsRepository;
        this.countryInformationRepository = countryInformationRepository;
        this.POINT_REFERENCE_LATITUDE = pointReferenceLatitude;
        this.POINT_REFERENCE_LONGITUDE = pointReferenceLongitude;
    }

    public IPInformationResponse getIpInformation(String ip){
        IPGeoLocation ipGeoLocation = geoConnector.getIPGeoLocation(ip);
        CountryInformation countryInfo = getCountryInformation(ipGeoLocation);
        Map<String, Double> currenciesRates = getCurrenciesInformationRates(countryInfo);
        IPInformationResponse informationResponse = buildIPInformationResponse(ip, ipGeoLocation, countryInfo, currenciesRates);
        newIpInvocation(ipGeoLocation, informationResponse);
        return informationResponse;
    }

    public Map<String, IPInvocationsPerCountry> getAllInvocations(){
        return invocationsRepository.getAllInvocations();
    }

    public IPInvocationsPerCountry getClosestInvocation(){
        LOGGER.info("Going to find the closest invocation from the history");
        Collection<IPInvocationsPerCountry> allInvocations = invocationsRepository.getAllInvocations().values();
        return allInvocations.stream()
                .min(Comparator.comparing(IPInvocationsPerCountry::getDistance))
                .orElse(new IPInvocationsPerCountry());
    }

    public IPInvocationsPerCountry getFarthestInvocation(){
        LOGGER.info("Going to find the farthest invocation from the history");
        Collection<IPInvocationsPerCountry> allInvocations = invocationsRepository.getAllInvocations().values();
        return allInvocations.stream()
                .max(Comparator.comparing(IPInvocationsPerCountry::getDistance))
                .orElse(new IPInvocationsPerCountry());
    }

    public AverageDistanceResponse getAverageInvocationDistance(){
        Collection<IPInvocationsPerCountry> allInvocations = invocationsRepository.getAllInvocations().values();
        Double totalDistance = allInvocations.stream().map(i -> i.getDistance() * i.getInvocations()).mapToDouble(Double::doubleValue).sum();
        Integer totalInvocations = allInvocations.stream().map(IPInvocationsPerCountry::getInvocations).mapToInt(Integer::intValue).sum();
        return new AverageDistanceResponse(totalDistance / totalInvocations);
    }

    private void newIpInvocation(IPGeoLocation ipGeoLocation, IPInformationResponse informationResponse) {
        InvocationCountryInformation invocationCountryInfo = new InvocationCountryInformation();
        invocationCountryInfo.setCountryCode(ipGeoLocation.getCountryCode3());
        invocationCountryInfo.setCountryName(informationResponse.getCountry());
        invocationCountryInfo.setDistance(informationResponse.getEstimatedDistanceFromReferencePointInKM());
        invocationsRepository.addIPInvocation(invocationCountryInfo);
    }

    private Map<String, Double> getCurrenciesInformationRates(CountryInformation countryInfo) {
        List<String> currencyCodes = countryInfo.getCurrencies().stream().map(CurrencyInformation::getCode).collect(Collectors.toList());
        try{
            return currenciesConnector.getCurrenciesRates(currencyCodes);
        } catch (Exception exc){
            LOGGER.error("Couldn't find the currencies {} of the country {}", currencyCodes, countryInfo.getName());
            return new HashMap<>();
        }
    }

    private CountryInformation getCountryInformation(IPGeoLocation ipGeoLocation){
        LOGGER.info("Going to find the country information for the country {}",ipGeoLocation.getCountryName());
        CountryInformation countryInformation = this.countryInformationRepository.getCountryInformation(ipGeoLocation.getCountryCode3());
        if(countryInformation == null){
            LOGGER.info("Country not found in the repository, looking in the Country Service");
            countryInformation = findCountryInformationFromService(ipGeoLocation);
            this.countryInformationRepository.newCountryInformation(countryInformation);
        }
        return countryInformation;
    }

    private CountryInformation findCountryInformationFromService(IPGeoLocation ipGeoLocation) {
        CountryInformation countryInformation;
        try{
            countryInformation = countriesConnector.getCountryInformation(ipGeoLocation.getCountryCode3());
        } catch (Exception exc){
            LOGGER.error("Couldn't find the country information of {} with code {}. Retrying with other county code", ipGeoLocation.getCountryName(), ipGeoLocation.getCountryCode());
            countryInformation = countriesConnector.getCountryInformation(ipGeoLocation.getCountryCode());
        }
        return countryInformation;
    }

    private IPInformationResponse buildIPInformationResponse(String ip, IPGeoLocation ipGeoLocation,
                                                    CountryInformation countryInfo, Map<String, Double> currenciesRates) {
        IPInformationResponse response = new IPInformationResponse();
        Date now = new Date();
        response.setIp(ip);
        response.setCurrentDate(now);
        response.setCountry(ipGeoLocation.getCountryName());
        response.setIsoCodes(getIsoCodes(countryInfo));
        response.setLanguages(getLanguages(countryInfo));
        response.setCurrencies(getCurrencyCodes(countryInfo));
        response.setCurrenciesRatesInUSD(currenciesRates);
        response.setTimeZones(countryInfo.getTimezones());
        response.setDatesWithTimeZone(buildDatesWithTimeZoneMap(now, countryInfo));
        response.setCoordinates(buildCoordinates(countryInfo));

        IPInvocationsPerCountry invocations = invocationsRepository.getIPInvocations(ipGeoLocation.getCountryCode3());
        if(invocations == null){
            response.setEstimatedDistanceFromReferencePointInKM(calculateDistanceFromReferencePoint(countryInfo));
        }else{
            response.setEstimatedDistanceFromReferencePointInKM(invocations.getDistance());
        }
        return response;
    }

    private List<List<Double>> buildCoordinates(CountryInformation countryInfo) {
        return Arrays.asList(Arrays.asList(POINT_REFERENCE_LATITUDE, POINT_REFERENCE_LONGITUDE),
                Arrays.asList(countryInfo.getLatitude().doubleValue(), countryInfo.getLongitude().doubleValue()));
    }

    private List<String> getIsoCodes(CountryInformation countryInfo){
        return countryInfo.getLanguages().stream().map(LanguageInformation::getIso639One).collect(Collectors.toList());
    }

    private List<String> getLanguages(CountryInformation countryInfo){
        return countryInfo.getLanguages().stream().map(LanguageInformation::getName).collect(Collectors.toList());
    }

    private List<String> getCurrencyCodes(CountryInformation countryInfo){
        return countryInfo.getCurrencies().stream().map(CurrencyInformation::getCode).collect(Collectors.toList());
    }

    private List<Map<String,String>> buildDatesWithTimeZoneMap(Date now, CountryInformation countryInfo){
        return countryInfo.getTimezones().stream()
                .map(t-> buildDateMap(now, t)).collect(Collectors.toList());
    }

    private Map<String,String> buildDateMap(Date now, String timezone){
        ZoneOffset zoneOffset = ZoneOffset.of(timezone.replace("UTC", ""));
        String formattedDate = now.toInstant().atOffset(zoneOffset).format(dateFormatter);
        return Map.ofEntries(Map.entry(timezone, formattedDate));
    }

    private double calculateDistanceFromReferencePoint(CountryInformation countryInfo){
        return GeoDistanceCalculator.getDistanceInKM(POINT_REFERENCE_LATITUDE, countryInfo.getLatitude(),
                POINT_REFERENCE_LONGITUDE, countryInfo.getLongitude());
    }

}
