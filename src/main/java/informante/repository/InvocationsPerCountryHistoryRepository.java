package informante.repository;

import informante.dto.IPInvocationsPerCountry;
import informante.dto.InvocationCountryInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InvocationsPerCountryHistoryRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvocationsPerCountryHistoryRepository.class);
    Map<String, IPInvocationsPerCountry> invocationsPerCountries;

    @Autowired
    public InvocationsPerCountryHistoryRepository() {
        this.invocationsPerCountries = new HashMap<>();
    }

    public IPInvocationsPerCountry getIPInvocations(String countryCode){
        LOGGER.info("Going to find the ip invocations history of the country {}", countryCode);
        return invocationsPerCountries.get(countryCode);
    }

    public Map<String, IPInvocationsPerCountry> getAllInvocations(){
        LOGGER.info("Going to find all the invocations");
        return invocationsPerCountries;
    }

    public void addIPInvocation(InvocationCountryInformation invocationCountryInformation){
        LOGGER.info("Going to add a new invocation for the country {}",invocationCountryInformation.getCountryName());
        IPInvocationsPerCountry invocations = getIPInvocations(invocationCountryInformation.getCountryCode());
        if(invocations == null){
            invocations = buildNewInvocationCountry(invocationCountryInformation);
            invocationsPerCountries.put(invocationCountryInformation.getCountryCode(), invocations);
        }else{
            invocations.newInvocation();
        }
    }

    private IPInvocationsPerCountry buildNewInvocationCountry(InvocationCountryInformation invocationCountryInformation) {
        IPInvocationsPerCountry newInvocation = new IPInvocationsPerCountry();
        newInvocation.setCountryName(invocationCountryInformation.getCountryName());
        newInvocation.setDistance(invocationCountryInformation.getDistance());
        newInvocation.setInvocations(1);
        return newInvocation;
    }


}
