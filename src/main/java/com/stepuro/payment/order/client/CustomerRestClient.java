package com.stepuro.payment.order.client;

import com.stepuro.payment.order.api.dto.ApiError;
import com.stepuro.payment.order.api.dto.TransferEntity;
import com.stepuro.payment.order.api.exceptions.ClientException;
import com.stepuro.payment.order.api.exceptions.ServerException;
import com.stepuro.payment.order.utils.wrapper.JacksonSerializeWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CustomerRestClient {
    private final RestTemplate restTemplate;

    @Value("${webclient.base.url.customer}")
    private String baseCustomerUrl;

    public CustomerRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendRestTransfer(TransferEntity transferEntity, String urlStart){
        var url = UriComponentsBuilder.fromUriString(baseCustomerUrl)
                .pathSegment(urlStart, "transfer_amount")
                .toUriString();

        HttpEntity<TransferEntity> httpEntity = new HttpEntity<>(transferEntity);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
        }
        catch (HttpClientErrorException e) {
            throw new ClientException(e.getMessage());
        }
        catch (HttpServerErrorException e) {
            throw new ServerException(e.getMessage());
        }
    }
}
