package informante.repository;

import informante.dto.CountryInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CountryInformationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryInformationRepository.class);
    private final Map<String, CountryInformation> countries;

    @Autowired
    public CountryInformationRepository() {
        this.countries = new HashMap<>();
    }

    public CountryInformation getCountryInformation(String countryCode){
        LOGGER.info("Going to find the country information for the country code {} in the repository", countryCode);
        return countries.get(countryCode);
    }

    public void newCountryInformation(CountryInformation countryInfo){
        LOGGER.info("Going to store a new countryInformation");
        this.countries.put(countryInfo.getCountryCode(), countryInfo);
    }

}
