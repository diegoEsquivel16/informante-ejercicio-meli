package informante.service;

import informante.connector.CurrenciesConnector;
import informante.dto.CurrencyServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrenciesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrenciesService.class);

    private final CurrencyServiceResponse snapshotCurrenciesResponse;
    private final CurrenciesConnector currenciesConnector;

    public CurrenciesService(CurrenciesConnector currenciesConnector) {
        this.currenciesConnector = currenciesConnector;
        this.snapshotCurrenciesResponse = currenciesConnector.getAllCurrencies();
    }

    public Map<String, Double> getCurrenciesRates(List<String> currencyCodes){
        LOGGER.info("Going to find the currencies for the country codes {}", currencyCodes);
        Map<String, Double> foundCurrencies = new HashMap<>();
        for (String currencyCode : currencyCodes) {
            foundCurrencies.put(currencyCode, this.snapshotCurrenciesResponse.getRates().get(currencyCode));
        }
        if(foundCurrencies.containsValue(null)){
            LOGGER.info("Couldn't find all country codes from the Snapshot, looking in the Currency Service");
            return currenciesConnector.getCurrenciesRates(currencyCodes);
        }else{
            return foundCurrencies;
        }
    }
}
