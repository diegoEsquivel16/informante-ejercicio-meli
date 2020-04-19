package informante.connector;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import informante.dto.CurrencyServiceResponse;
import informante.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CurrenciesConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrenciesConnector.class);
    private static final String LATEST_RATES_PATH = "/latest?";
    private static final String BASE_PATH = "base=";
    private static final String SYMBOLS_PATH = "symbols=";

    private final RestClient client;
    private final ObjectMapper mapper;
    private final String DEFAULT_BASE;
    private CurrencyServiceResponse snapshotCurrenciesResponse;

    @Autowired
    public CurrenciesConnector(@Value("${currencies-connector-host}") String currenciesConnectorHost,
                               @Value("${currencies-connector-connection-timeout-in-seconds}") long connectionTimeout,
                               @Value("${currencies-connector-default-base}") String defaultBase) {
        this.client = new RestClient(currenciesConnectorHost, connectionTimeout);
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.DEFAULT_BASE = defaultBase;
    }

    @PostConstruct
    private void buildSnapshot(){
        this.snapshotCurrenciesResponse = getAllCurrencies();
    }

    public CurrencyServiceResponse getAllCurrencies(){
        LOGGER.info("Going to find all the currencies ");
        String stringResponse = client.getAsString(buildLatestRatesPath());
        try{
            return mapper.readValue(stringResponse, CurrencyServiceResponse.class);
        } catch (IOException ioExc){
            LOGGER.error("Couldn't parse the currencies response {}", stringResponse);
            throw new ServiceException(ioExc.getMessage());
        }
    }

    public Map<String, Double> getCurrenciesRates(List<String> currencyCodes){
        LOGGER.info("Going to find the currencies for the country codes {}", currencyCodes);
        Map<String, Double> foundCurrencies = new HashMap<>();
        for (String currencyCode : currencyCodes) {
            foundCurrencies.put(currencyCode, this.snapshotCurrenciesResponse.getRates().get(currencyCode));
        }
        if(foundCurrencies.containsValue(null)){
            LOGGER.info("Couldn't find all country codes from the Snapshot, looking in the Currency Service");
            return getCurrenciesRatesFromService(currencyCodes);
        }else{
            return foundCurrencies;
        }
    }

    private Map<String, Double> getCurrenciesRatesFromService(List<String> currencyCodes){
        LOGGER.info("Going to find the latest currencies of {} from the service",currencyCodes);
        String stringResponse = client.getAsString(buildLatestRatesPathForCurrencies(currencyCodes));
        try{
            return mapper.readValue(stringResponse, CurrencyServiceResponse.class).getRates();
        } catch (IOException ioExc){
            LOGGER.error("Couldn't parse the currencies response {}", stringResponse);
            throw new ServiceException(ioExc.getMessage());
        }
    }

    private String buildLatestRatesPath() {
        return LATEST_RATES_PATH + BASE_PATH + DEFAULT_BASE;
    }

    private String buildLatestRatesPathForCurrencies(List<String> currencyCodes) {
        return LATEST_RATES_PATH + BASE_PATH + DEFAULT_BASE +
                "&" + SYMBOLS_PATH + String.join(",", currencyCodes);
    }
}
