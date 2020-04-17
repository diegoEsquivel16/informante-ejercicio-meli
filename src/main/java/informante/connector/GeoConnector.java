package informante.connector;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import informante.dto.IPGeoLocation;
import informante.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GeoConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeoConnector.class);
    private static String IP_PATH = "/ip?";

    private final RestClient client;
    private final ObjectMapper mapper;
    //https://api.ip2country.info/ip?5.6.7.8
    /*
    * {
countryCode: "DE",
countryCode3: "DEU",
countryName: "Germany",
countryEmoji: "🇩🇪"
}
    * */
    public GeoConnector(String geoConnectorHost) {
        this.client = new RestClient(geoConnectorHost);
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
