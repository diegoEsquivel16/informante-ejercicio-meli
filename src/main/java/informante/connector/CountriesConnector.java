package informante.connector;

import informante.dto.CountryInformation;

public class CountriesConnector {

    private String countriesConnectorHost;
    //http://restcountries.eu/rest/v2/alpha/arg

    public CountriesConnector(String countriesConnectorHost) {
        this.countriesConnectorHost = countriesConnectorHost;
    }

    public CountryInformation getCountryInformation(String countryCode){
        return new CountryInformation();
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