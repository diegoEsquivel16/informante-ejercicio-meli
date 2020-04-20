package informante.service;

import informante.connector.GeoConnector;
import informante.dto.*;
import informante.repository.InvocationsPerCountryHistoryRepository;
import informante.utils.IPInformationResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InformanteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InformanteService.class);
    private final GeoConnector geoConnector;
    private final CountryInformationService countryInformationService;
    private final CurrenciesService currenciesService;
    private final InvocationsPerCountryHistoryRepository invocationsRepository;
    private final IPInformationResponseBuilder ipInformationResponseBuilder;
    private AverageDistanceResponse averageDistance;

    @Autowired
    public InformanteService(GeoConnector geoConnector, CountryInformationService countryInformationService,
                             CurrenciesService currenciesService, InvocationsPerCountryHistoryRepository invocationsRepository,
                             IPInformationResponseBuilder ipInformationResponseBuilder) {
        this.geoConnector = geoConnector;
        this.countryInformationService = countryInformationService;
        this.currenciesService = currenciesService;
        this.invocationsRepository = invocationsRepository;
        this.ipInformationResponseBuilder = ipInformationResponseBuilder;
    }

    @PostConstruct
    private void initializeAverageDistance(){
        this.averageDistance = new AverageDistanceResponse();
        this.updateAverageInvocationDistance();
    }

    public IPInformationResponse getIpInformation(String ip){
        IPGeoLocation ipGeoLocation = this.geoConnector.getIPGeoLocation(ip);
        CountryInformation countryInfo = this.countryInformationService.getCountryInformation(ipGeoLocation);
        Map<String, Double> currenciesRates = getCurrenciesInformationRates(countryInfo);
        IPInformationResponse informationResponse = this.ipInformationResponseBuilder.buildIPInformationResponse(ip, ipGeoLocation, countryInfo, currenciesRates);
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
        return averageDistance;
    }

    @Scheduled(fixedRateString = "${fixed-rate-update-average-task-in-milliseconds}")
    private void updateAverageInvocationDistance(){
        LOGGER.info("Going to calculate the average invocations distance");
        Collection<IPInvocationsPerCountry> allInvocations = invocationsRepository.getAllInvocations().values();
        Double totalDistance = allInvocations.stream().map(i -> i.getDistance() * i.getInvocations()).mapToDouble(Double::doubleValue).sum();
        Integer totalInvocations = allInvocations.stream().map(IPInvocationsPerCountry::getInvocations).mapToInt(Integer::intValue).sum();
        this.averageDistance.setAverageDistance((totalInvocations == 0) ? 0 : totalDistance / totalInvocations);
    }

    private Map<String, Double> getCurrenciesInformationRates(CountryInformation countryInfo) {
        List<String> currencyCodes = countryInfo.getCurrencies().stream().map(CurrencyInformation::getCode).collect(Collectors.toList());
        try{
            return currenciesService.getCurrenciesRates(currencyCodes);
        } catch (Exception exc){
            LOGGER.error("Couldn't find the currencies {} of the country {}", currencyCodes, countryInfo.getName());
            return new HashMap<>();
        }
    }

    private void newIpInvocation(IPGeoLocation ipGeoLocation, IPInformationResponse informationResponse) {
        InvocationCountryInformation invocationCountryInfo = new InvocationCountryInformation();
        invocationCountryInfo.setCountryCode(ipGeoLocation.getCountryCode3());
        invocationCountryInfo.setCountryName(informationResponse.getCountry());
        invocationCountryInfo.setDistance(informationResponse.getEstimatedDistanceFromReferencePointInKM());
        invocationsRepository.addIPInvocation(invocationCountryInfo);
    }

}
