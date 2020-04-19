package informante.controller;

import informante.dto.AverageDistanceResponse;
import informante.dto.IPInformationResponse;
import informante.dto.IPInvocationsPerCountry;
import informante.exception.ServiceException;
import informante.service.InformanteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/informante")
public class InformanteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InformanteController.class);

    private final InformanteService informanteService;

    public InformanteController(InformanteService informanteService) {
        this.informanteService = informanteService;
    }

    @GetMapping("/ip/{ip}")
    public ResponseEntity<IPInformationResponse> getIPInformation(@PathVariable String ip){
        try{
            IPInformationResponse informationResponse = informanteService.getIpInformation(ip);
            return ResponseEntity.ok(informationResponse);
        } catch (ServiceException se){
            LOGGER.error("Couldn't get the IP information of {}, exception {}", ip, se.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/invocations/history")
    public ResponseEntity<Map<String, IPInvocationsPerCountry>> getAllInvocations(){
        return ResponseEntity.ok(informanteService.getAllInvocations());
    }

    @GetMapping("/invocations/closest")
    public ResponseEntity<IPInvocationsPerCountry> getClosestInvocation(){
        return ResponseEntity.ok(informanteService.getClosestInvocation());
    }

    @GetMapping("/invocations/farthest")
    public ResponseEntity<IPInvocationsPerCountry> getFarthestInvocation(){
        return ResponseEntity.ok(informanteService.getFarthestInvocation());
    }

    @GetMapping("/invocations/average-distance")
    public ResponseEntity<AverageDistanceResponse> getAverageDistanceInvocation(){
        return ResponseEntity.ok(informanteService.getAverageInvocationDistance());
    }

}
