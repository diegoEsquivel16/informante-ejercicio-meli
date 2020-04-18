package informante.connector;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import informante.dto.CountryInformation;
import informante.dto.CurrencyServiceResponse;
import informante.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class CurrenciesConnector {

    //https://api.exchangeratesapi.io/latest?base=USD
    //https://api.exchangeratesapi.io/latest?base=USD&symbols=BRL
/*
{
rates: {
BRL: 5.2300184162
},
base: "USD",
date: "2020-04-17"
}
*/
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrenciesConnector.class);
    private static final String LATEST_RATES_PATH = "/latest?";
    private static final String BASE_PATH = "base=";
    private static final String SYMBOLS_PATH = "symbols=";

    private final RestClient client;
    private final ObjectMapper mapper;

    public CurrenciesConnector(String currenciesConnectorHost) {
        this.client = new RestClient(currenciesConnectorHost);
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public CurrencyServiceResponse getAllCurrenciesWithUSDAsBase(){
        return getAllCurrencies("USD");
    }

    public CurrencyServiceResponse getAllCurrencies(String baseCurrencyCode){
        LOGGER.info("Going to find all the currencies ");
        String stringResponse = client.getAsString(buildLatestRatesPath(baseCurrencyCode));
        try{
            return mapper.readValue(stringResponse, CurrencyServiceResponse.class);
        } catch (IOException ioExc){
            LOGGER.error("Couldn't parse the currencies response {}", stringResponse);
            throw new ServiceException(ioExc.getMessage());
        }
    }

    public CurrencyServiceResponse getCurrenciesWithUSDAsBase(List<String> currencyCodes){
        return getCurrencies("USD", currencyCodes);
    }

    public CurrencyServiceResponse getCurrencies(String baseCurrencyCode, List<String> currencyCodes){
        LOGGER.info("Going to find the latest currencies of {}",currencyCodes);
        String stringResponse = client.getAsString(buildLatestRatesPathForCurrencies(baseCurrencyCode, currencyCodes));
        try{
            return mapper.readValue(stringResponse, CurrencyServiceResponse.class);
        } catch (IOException ioExc){
            LOGGER.error("Couldn't parse the currencies response {}", stringResponse);
            throw new ServiceException(ioExc.getMessage());
        }
    }

    private String buildLatestRatesPath(String baseCurrency) {
        return LATEST_RATES_PATH + BASE_PATH + baseCurrency;
    }

    private String buildLatestRatesPathForCurrencies(String baseCurrency, List<String> currencyCodes) {
        return LATEST_RATES_PATH + BASE_PATH + baseCurrency +
                "&" + SYMBOLS_PATH + String.join(",", currencyCodes);
    }
}
