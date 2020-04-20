package informante.utils;

import informante.dto.*;
import informante.repository.InvocationsPerCountryHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IPInformationResponseBuilder {

    private final double POINT_REFERENCE_LATITUDE;
    private final double POINT_REFERENCE_LONGITUDE;
    private final DateTimeFormatter dateFormatter;
    private final InvocationsPerCountryHistoryRepository invocationsRepository;

    @Autowired
    public IPInformationResponseBuilder(@Value("${point-reference-latitude}") double pointReferenceLatitude,
                                        @Value("${point-reference-longitude}") double pointReferenceLongitude,
                                        @Value("${date-formatter-pattern}") String dateFormatterPattern,
                                        InvocationsPerCountryHistoryRepository invocationsRepository) {
        this.POINT_REFERENCE_LATITUDE = pointReferenceLatitude;
        this.POINT_REFERENCE_LONGITUDE = pointReferenceLongitude;
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormatterPattern);
        this.invocationsRepository = invocationsRepository;
    }

    public IPInformationResponse buildIPInformationResponse(String ip, IPGeoLocation ipGeoLocation,
                                                            CountryInformation countryInfo, Map<String, Double> currenciesRates) {
        IPInformationResponse response = new IPInformationResponse();
        Date now = new Date();
        response.setIp(ip);
        response.setCurrentDate(now.toString());
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
