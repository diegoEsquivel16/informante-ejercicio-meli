package informante;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import informante.connector.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InformanteConfig {

    @Bean
    public ObjectMapper buildObjectMapper(){
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean("countries-connector-rest-client")
    public RestClient buildRestClientForCountriesConnector(@Value("${countries-connector-host}") String countriesConnectorHost,
                                                          @Value("${countries-connector-connection-timeout-in-seconds}") long connectionTimeout){
        return new RestClient(countriesConnectorHost, connectionTimeout);
    }

    @Bean("currencies-connector-rest-client")
    public RestClient buildRestClientForCurrenciesConnector(@Value("${currencies-connector-host}") String currenciesConnectorHost,
                                                          @Value("${currencies-connector-connection-timeout-in-seconds}") long connectionTimeout){
        return new RestClient(currenciesConnectorHost, connectionTimeout);
    }

    @Bean("geo-connector-rest-client")
    public RestClient buildRestClientForGeoConnector(@Value("${geo-connector-host}") String geoConnectorHost,
                                                           @Value("${geo-connector-connection-timeout-in-seconds}") long connectionTimeout){
        return new RestClient(geoConnectorHost, connectionTimeout);
    }
}
