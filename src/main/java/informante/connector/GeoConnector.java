package informante.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import informante.dto.IPGeoLocation;
import informante.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GeoConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoConnector.class);
    private static final String IP_PATH = "/ip?";

    private final RestClient client;
    private final ObjectMapper mapper;

    @Autowired
    public GeoConnector(@Qualifier("geo-connector-rest-client") RestClient restClient,
                        ObjectMapper objectMapper) {
        this.client = restClient;
        this.mapper = objectMapper;
    }

    public IPGeoLocation getIPGeoLocation(String ip){
        LOGGER.info("Going to find the geo location for the IP {}", ip);
        String stringResponse = client.getAsString(IP_PATH+ip);
        IPGeoLocation ipGeoLocation;
        try{
            ipGeoLocation = mapper.readValue(stringResponse, IPGeoLocation.class);
        } catch (IOException ioExc){
            LOGGER.error("Couldn't parse the ip geolocation response {}", stringResponse);
            throw new ServiceException(ioExc.getMessage());
        }
        validateResponse(ipGeoLocation);
        return ipGeoLocation;
    }

    private void validateResponse(IPGeoLocation ipGeoLocation) {
        if(ipGeoLocation.getCountryCode().isBlank()){
            throw new ServiceException("The country code is blank!");
        }
        if(ipGeoLocation.getCountryCode3().isBlank()){
            throw new ServiceException("The country code 3 is blank!");
        }
        if(ipGeoLocation.getCountryName().isBlank()){
            throw new ServiceException("The country name is blank!");
        }
    }
}
