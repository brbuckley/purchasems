package purchasems.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import purchasems.PurchaseMsResponseUtil;
import purchasems.api.model.product.ProductSupplier;
import purchasems.configuration.ProductMsConfig;
import purchasems.exception.ApiIsDownException;

public class ProductServiceTest {

  ProductService service;

  @Test
  public void testGetFromProductMs_whenValid_thenReturnProducts() throws Exception {
    ResponseEntity<List<ProductSupplier>> response =
        new ResponseEntity<List<ProductSupplier>>(
            PurchaseMsResponseUtil.productSupplierList(), HttpStatus.OK);
    // Mocks
    RestTemplate mocked = Mockito.mock(RestTemplate.class);
    when(mocked.exchange(
            "endpoint?fetch-suppliers=true&ids=PRD0000001,PRD0000003,PRD0000002",
            HttpMethod.GET,
            PurchaseMsResponseUtil.defaultRequestHeaders(),
            new ParameterizedTypeReference<List<ProductSupplier>>() {}))
        .thenReturn(response);
    when(mocked.postForObject(
            "tokenEndpoint", PurchaseMsResponseUtil.defaultTokenRequest(), String.class))
        .thenReturn("{\"access_token\":\"token\"}");

    service =
        new ProductService(
            new ProductMsConfig("endpoint", "tokenEndpoint", "audience", "id", "secret"),
            new ObjectMapper(),
            mocked);

    List<ProductSupplier> products =
        service.getFromProductMs(PurchaseMsResponseUtil.productIdList());

    assertEquals(3, products.size());
  }

  @Test
  public void testCallForProduct_whenProductMsDown_thenThrow() {
    // Mocks
    RestTemplate mocked = Mockito.mock(RestTemplate.class);
    when(mocked.exchange(
            "endpoint?fetch-suppliers=true&ids=PRD0000001,PRD0000003,PRD0000002",
            HttpMethod.GET,
            PurchaseMsResponseUtil.defaultRequestHeaders(),
            new ParameterizedTypeReference<List<ProductSupplier>>() {}))
        .thenThrow(new ResourceAccessException("ProductMS is down!"));

    service =
        new ProductService(
            new ProductMsConfig("endpoint", "tokenEndpoint", "audience", "id", "secret"),
            new ObjectMapper(),
            mocked);
    service.setToken("token");

    assertThrows(
        ApiIsDownException.class,
        () -> service.getFromProductMs(PurchaseMsResponseUtil.productIdList()));
  }

  @Test
  public void testGetFromProductMs_whenNotFound_thenThrow() {
    // Mocks
    RestTemplate mocked = Mockito.mock(RestTemplate.class);
    when(mocked.exchange(
            "endpoint?fetch-suppliers=true&ids=PRD0000001,PRD0000003,PRD0000002",
            HttpMethod.GET,
            PurchaseMsResponseUtil.defaultRequestHeaders(),
            new ParameterizedTypeReference<List<ProductSupplier>>() {}))
        .thenThrow(new RestClientException("Product nor found!"));

    service =
        new ProductService(
            new ProductMsConfig("endpoint", "tokenEndpoint", "audience", "id", "secret"),
            new ObjectMapper(),
            mocked);
    service.setToken("token");

    assertThrows(
        AmqpRejectAndDontRequeueException.class,
        () -> service.getFromProductMs(PurchaseMsResponseUtil.productIdList()));
  }

  @Test
  public void testCallForProduct_whenNullArray_thenThrow() {
    List<ProductSupplier> nullProduct = null;
    ResponseEntity<List<ProductSupplier>> response =
        new ResponseEntity<List<ProductSupplier>>(nullProduct, HttpStatus.OK);

    // Mocks
    RestTemplate mocked = Mockito.mock(RestTemplate.class);
    when(mocked.exchange(
            "endpoint?fetch-suppliers=true&ids=PRD0000001,PRD0000003,PRD0000002",
            HttpMethod.GET,
            PurchaseMsResponseUtil.defaultRequestHeaders(),
            new ParameterizedTypeReference<List<ProductSupplier>>() {}))
        .thenReturn(response);

    service =
        new ProductService(
            new ProductMsConfig("endpoint", "tokenEndpoint", "audience", "id", "secret"),
            new ObjectMapper(),
            mocked);
    service.setToken("token");

    assertThrows(
        AmqpRejectAndDontRequeueException.class,
        () -> service.getFromProductMs(PurchaseMsResponseUtil.productIdList()));
  }

  @Test
  public void testGetFromProductMs_whenTokenExpired_thenTryAgain() throws Exception {
    ResponseEntity<List<ProductSupplier>> response =
        new ResponseEntity<List<ProductSupplier>>(
            PurchaseMsResponseUtil.productSupplierList(), HttpStatus.OK);

    // Mocks
    RestTemplate mocked = Mockito.mock(RestTemplate.class);
    when(mocked.exchange(
            "endpoint?fetch-suppliers=true&ids=PRD0000001,PRD0000003,PRD0000002",
            HttpMethod.GET,
            PurchaseMsResponseUtil.defaultRequestHeaders(),
            new ParameterizedTypeReference<List<ProductSupplier>>() {}))
        .thenThrow(mock(HttpClientErrorException.Unauthorized.class));
    when(mocked.postForObject(
            "tokenEndpoint", PurchaseMsResponseUtil.defaultTokenRequest(), String.class))
        .thenReturn("{\"access_token\":\"new token\"}");
    when(mocked.exchange(
            "endpoint?fetch-suppliers=true&ids=PRD0000001,PRD0000003,PRD0000002",
            HttpMethod.GET,
            PurchaseMsResponseUtil.anotherRequestHeaders(),
            new ParameterizedTypeReference<List<ProductSupplier>>() {}))
        .thenReturn(response);

    service =
        new ProductService(
            new ProductMsConfig("endpoint", "tokenEndpoint", "audience", "id", "secret"),
            new ObjectMapper(),
            mocked);
    service.setToken("token");

    List<ProductSupplier> products =
        service.getFromProductMs(PurchaseMsResponseUtil.productIdList());

    assertEquals(3, products.size());
  }
}
