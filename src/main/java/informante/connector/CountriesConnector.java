package informante.connector;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import informante.dto.CountryInformation;
import informante.dto.IPGeoLocation;
import informante.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CountriesConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountriesConnector.class);
    private static final String COUNTRY_INFO_PATH = "/rest/v2/alpha/";

    private final RestClient client;
    private final ObjectMapper mapper;
    //http://restcountries.eu/rest/v2/alpha/arg

    public CountriesConnector(String countriesConnectorHost) {
        this.client = new RestClient(countriesConnectorHost);
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

/* full response
* {
name: "Argentina",
topLevelDomain: [
".ar"
],
alpha2Code: "AR",
alpha3Code: "ARG",
callingCodes: [
"54"
],
capital: "Buenos Aires",
altSpellings: [
"AR",
"Argentine Republic",
"República Argentina"
],
region: "Americas",
subregion: "South America",
population: 43590400,
latlng: [
-34,
-64
],
demonym: "Argentinean",
area: 2780400,
gini: 44.5,
timezones: [
"UTC-03:00"
],
borders: [
"BOL",
"BRA",
"CHL",
"PRY",
"URY"
],
nativeName: "Argentina",
numericCode: "032",
currencies: [
{
code: "ARS",
name: "Argentine peso",
symbol: "$"
}
],
languages: [
{
iso639_1: "es",
iso639_2: "spa",
name: "Spanish",
nativeName: "Español"
},
{
iso639_1: "gn",
iso639_2: "grn",
name: "Guaraní",
nativeName: "Avañe'ẽ"
}
],
translations: {
de: "Argentinien",
es: "Argentina",
fr: "Argentine",
ja: "アルゼンチン",
it: "Argentina",
br: "Argentina",
pt: "Argentina",
nl: "Argentinië",
hr: "Argentina",
fa: "آرژانتین"
},
flag: "https://restcountries.eu/data/arg.svg",
regionalBlocs: [
{
acronym: "USAN",
name: "Union of South American Nations",
otherAcronyms: [
"UNASUR",
"UNASUL",
"UZAN"
],
otherNames: [
"Unión de Naciones Suramericanas",
"União de Nações Sul-Americanas",
"Unie van Zuid-Amerikaanse Naties",
"South American Union"
]
}
],
cioc: "ARG"
}*/