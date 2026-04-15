package com.cmanager.app.integration.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AbstractRequest<T> {

    private final RestTemplate restTemplate;

    public AbstractRequest(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    public T getShow(String url, ParameterizedTypeReference<T> typeReference) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, null, typeReference);
            return response.getBody();
        }catch (HttpClientErrorException ex) {
            throw new RuntimeException("Erro HTTP: " + ex.getStatusCode(), ex);
        }catch (Exception ex) {
            throw new RuntimeException("Erro inesperado ao consumir API", ex);
        }



    }
}
