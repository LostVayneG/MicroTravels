package com.microsystem.ProviderService.Controllers;

import com.microsystem.ProviderService.Model.Provider;
import com.microsystem.ProviderService.Repository.IProvidersRepostitory;
import com.microsystem.ProviderService.Request.ProviderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
public class ProvidersController {
    @Autowired
    private IProvidersRepostitory providersRepostitory;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Environment environment;

    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @RequestMapping(
        value = "/providers/status",
        produces = MediaType.APPLICATION_JSON 
    )
    public String getStatus(){
        String port = environment.getProperty("local.server.port");
        return "Server is up on port " + port;
    }

    @PostMapping("/providers")
    @Consumes(MediaType.APPLICATION_JSON)
    public ResponseEntity createProvider(@RequestBody ProviderRequest providerRequest){
        Provider provider = new Provider(
                providerRequest.getName(),
                providerRequest.getUserName(),
                providerRequest.getAge(),
                providerRequest.getPhoto(),
                providerRequest.getDescription(),
                providerRequest.getPhoneNumber(),
                providerRequest.getWebPage(),
                providerRequest.getSocialNetwork());
        providersRepostitory.save(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body("Creado Correctamente");
    }

    @GetMapping("/providers")
    public ResponseEntity getProviders(){
        List<Provider> providers = new ArrayList<>();
        providersRepostitory.findAll().forEach(provider -> providers.add(provider));
        return ResponseEntity.status(HttpStatus.FOUND).body(providers);
    }

    @PutMapping("/providers/{idProvider}")
    public ResponseEntity updateProvider(@PathVariable("idProvider") int idProvider, @RequestBody ProviderRequest providerRequest){
        Optional<Provider> optionalProvider = providersRepostitory.findById(idProvider);
        if(optionalProvider.isPresent()){
            Provider provider = optionalProvider.get();
            provider.updateProvider(
                    providerRequest.getName(),
                    providerRequest.getAge(),
                    providerRequest.getPhoto(),
                    providerRequest.getDescription(),
                    providerRequest.getPhoneNumber(),
                    providerRequest.getWebPage(),
                    providerRequest.getSocialNetwork());
            providersRepostitory.save(provider);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Se actualizo el proveedor");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el proveedor con el id "+idProvider);
        }
    }
    @GetMapping("/providers/{idProvider}")
    public ResponseEntity getProviderById(@PathVariable("idProvider") int idProvider){
        Optional<Provider> optionalProvider = providersRepostitory.findById(idProvider);
        if(optionalProvider.isPresent()){
            Provider provider = optionalProvider.get();
            return ResponseEntity.status(HttpStatus.FOUND).body(provider);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el proveedor con el id "+idProvider);
        }
    }

    @GetMapping("/providers/{idProvider}/services")
    public ResponseEntity getServicesByProviderId(){
        //TODO: Llamar al servicio services
        return null;
    }

    @PostMapping("/providers/{idProvider}/services")
    public ResponseEntity createServicesByProviderId(){
        //TODO: Llamar al servicio services
        return null;
    }
}
