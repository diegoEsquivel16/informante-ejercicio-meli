package informante.connector;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import informante.dto.CountryInformation;
import informante.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CountriesConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountriesConnector.class);
    private static final String COUNTRY_INFO_PATH = "/rest/v2/alpha/";

    private final RestClient client;
    private final ObjectMapper mapper;

    @Autowired
    public CountriesConnector(@Qualifier("countries-connector-rest-client") RestClient restClient,
                              ObjectMapper objectMapper) {
        this.client = restClient;
        this.mapper = objectMapper;
    }

    public CountryInformation getCountryInformation(String countryCode){
        LOGGER.info("Going to find the country information of {}", countryCode);
        String stringResponse = client.getAsString(COUNTRY_INFO_PATH+countryCode);
        try{
           return mapper.readValue(stringResponse, CountryInformation.class);
        } catch (IOException ioExc){
            LOGGER.error("Couldn't parse the country information response {}", stringResponse);
            throw new ServiceException(ioExc.getMessage());
        }
    }

}