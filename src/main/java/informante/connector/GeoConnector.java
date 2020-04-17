package informante.connector;

import informante.dto.IPGeoLocation;

public class GeoConnector {

    private String geoConnectorHost;
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
        this.geoConnectorHost = geoConnectorHost;
    }

    public IPGeoLocation getIPGeoLocation(String ip){
        return new IPGeoLocation();
    }
}
