package informante.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import informante.dto.CurrencyServiceResponse;
import informante.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    @Autowired
    public CurrenciesConnector(@Qualifier("currencies-connector-rest-client") RestClient restClient,
                               ObjectMapper objectMapper, @Value("${currencies-connector-default-base}") String defaultBase) {
        this.client = restClient;
        this.mapper = objectMapper;
        this.DEFAULT_BASE = defaultBase;
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
