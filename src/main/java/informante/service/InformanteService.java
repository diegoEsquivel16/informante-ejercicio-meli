package informante.service;

import informante.dto.IPInformationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InformanteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InformanteService.class);

    public IPInformationResponse getIpInformation(String ip){
        return new IPInformationResponse();
    }
}
