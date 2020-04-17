package informante.controller;

import informante.dto.IPInformationResponse;
import informante.exception.ServiceException;
import informante.service.InformanteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

public class InformanteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InformanteController.class);

    private InformanteService informanteService;

    public InformanteController(InformanteService informanteService) {
        this.informanteService = informanteService;
    }

    public ResponseEntity<IPInformationResponse> getIPInformation(String ip){
        try{
            IPInformationResponse informationResponse = informanteService.getIpInformation(ip);
            return ResponseEntity.ok(informationResponse);
        } catch (ServiceException se){
            LOGGER.error("Couldn't get the IP information of {}, exception {}", ip, se.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
