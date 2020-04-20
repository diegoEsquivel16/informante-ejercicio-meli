package informante.service;

import informante.connector.CountriesConnector;
import informante.dto.CountryInformation;
import informante.dto.IPGeoLocation;
import informante.repository.CountryInformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryInformationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryInformationService.class);

    private final CountriesConnector countriesConnector;
    private final CountryInformationRepository countryInformationRepository;

    @Autowired
    public CountryInformationService(CountriesConnector countriesConnector, CountryInformationRepository countryInformationRepository) {
        this.countriesConnector = countriesConnector;
        this.countryInformationRepository = countryInformationRepository;
    }

    public CountryInformation getCountryInformation(IPGeoLocation ipGeoLocation){
        LOGGER.info("Going to find the country information for the country {}",ipGeoLocation.getCountryName());
        CountryInformation countryInformation = this.countryInformationRepository.getCountryInformation(ipGeoLocation.getCountryCode3());
        if(countryInformation == null){
            LOGGER.info("Country not found in the repository, looking in the Country Service");
            countryInformation = findCountryInformationFromService(ipGeoLocation);
            this.countryInformationRepository.newCountryInformation(countryInformation);
        }
        return countryInformation;
    }

    private CountryInformation findCountryInformationFromService(IPGeoLocation ipGeoLocation) {
        CountryInformation countryInformation;
        try{
            countryInformation = countriesConnector.getCountryInformation(ipGeoLocation.getCountryCode3());
        } catch (Exception exc){
            LOGGER.error("Couldn't find the country information of {} with code {}. Retrying with other county code", ipGeoLocation.getCountryName(), ipGeoLocation.getCountryCode());
            countryInformation = countriesConnector.getCountryInformation(ipGeoLocation.getCountryCode());
        }
        return countryInformation;
    }

}
