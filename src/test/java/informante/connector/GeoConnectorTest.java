package informante.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import informante.dto.IPGeoLocation;
import informante.exception.RestClientHttpException;
import informante.exception.ServiceException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class GeoConnectorTest {
    @Mock
    RestClient client;
    @Mock
    ObjectMapper mapper;
    @InjectMocks
    GeoConnector geoConnector;

    @BeforeTest
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expectedExceptions = ServiceException.class, expectedExceptionsMessageRegExp = "The Geo Service response it isn't a valid one!")
    public void ifGetInvalidGeoResponseShouldThrowException() throws IOException{
        when(client.getAsString(anyString())).thenReturn("invalidResponse");
        when(mapper.readValue(anyString(), eq(IPGeoLocation.class))).thenReturn(null);
        geoConnector.getIPGeoLocation("ip");
    }

    @Test(expectedExceptions = ServiceException.class, expectedExceptionsMessageRegExp = "The country code is blank!")
    public void ifGetBlankCountryCodeShouldThrowException() throws IOException {
        IPGeoLocation geoResponse = buildIPGeoLocation();
        geoResponse.setCountryCode(null);
        when(client.getAsString(anyString())).thenReturn("");
        when(mapper.readValue(anyString(), eq(IPGeoLocation.class))).thenReturn(geoResponse);
        geoConnector.getIPGeoLocation("ip");
    }

    @Test(expectedExceptions = ServiceException.class, expectedExceptionsMessageRegExp = "The country code 3 is blank!")
    public void ifGetBlankCountryCode3ShouldThrowException() throws IOException {
        IPGeoLocation geoResponse = buildIPGeoLocation();
        geoResponse.setCountryCode3(null);
        when(client.getAsString(anyString())).thenReturn("");
        when(mapper.readValue(anyString(), eq(IPGeoLocation.class))).thenReturn(geoResponse);
        geoConnector.getIPGeoLocation("ip");
    }

    @Test(expectedExceptions = ServiceException.class, expectedExceptionsMessageRegExp = "The country name is blank!")
    public void ifGetBlankCountryNameShouldThrowException() throws IOException {
        IPGeoLocation geoResponse = buildIPGeoLocation();
        geoResponse.setCountryName(null);
        when(client.getAsString(anyString())).thenReturn("");
        when(mapper.readValue(anyString(), eq(IPGeoLocation.class))).thenReturn(geoResponse);
        geoConnector.getIPGeoLocation("ip");
    }

    @Test(expectedExceptions = ServiceException.class)
    public void ifMapperParseFailsShouldThrowException() throws IOException{
        when(client.getAsString(anyString())).thenReturn("");
        when(mapper.readValue(anyString(), eq(IPGeoLocation.class))).thenThrow(IOException.class);
        geoConnector.getIPGeoLocation("ip");
    }

    @Test(expectedExceptions = RestClientHttpException.class)
    public void ifRestClientFailsShouldThrowException() {
        when(client.getAsString(anyString())).thenThrow(RestClientHttpException.class);
        geoConnector.getIPGeoLocation("ip");
    }

    private IPGeoLocation buildIPGeoLocation(){
        IPGeoLocation ipGeoLocation = new IPGeoLocation();
        ipGeoLocation.setCountryCode("AR");
        ipGeoLocation.setCountryCode3("ARG");
        ipGeoLocation.setCountryName("Argentina");

        return ipGeoLocation;
    }
}
