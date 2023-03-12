package purchasems.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import purchasems.api.model.product.ProductSupplier;
import purchasems.configuration.ProductMsConfig;
import purchasems.exception.ApiIsDownException;
import purchasems.exception.ExpiredTokenException;
import purchasems.exception.NotExistException;

/** Product services. */
@Service
@Slf4j
public class ProductService {

  private final ProductMsConfig config;
  private final ObjectMapper mapper;
  private final RestTemplate restTemplate;

  @Setter private String token;

  /**
   * Custom constructor, injects dependencies and initializes token as null.
   *
   * @param config ProductMs configurations.
   * @param mapper Object mapper.
   * @param restTemplate Rest template.
   */
  public ProductService(ProductMsConfig config, ObjectMapper mapper, RestTemplate restTemplate) {
    this.config = config;
    this.mapper = mapper;
    this.restTemplate = restTemplate;
    this.token = null;
  }

  /**
   * Fetches the products from the given order from the DataBase.
   *
   * @param ids List of product ids.
   * @return List of Products.
   * @throws NotExistException Not Exist Exception.
   */
  public List<ProductSupplier> getFromProductMs(List<String> ids) throws Exception {
    StringBuilder builder = new StringBuilder();
    for (String id : ids) {
      if (builder.length() != 0) {
        builder.append(",");
      }
      builder.append(id);
    }
    String idsString = builder.toString();
    if (token == null) {
      token = getToken();
    }
    try {
      return callForProduct(idsString);
    } catch (ExpiredTokenException tokenException) {
      // Token expired, get another one and try again
      token = getToken();
      return callForProduct(idsString);
    }
  }

  private List<ProductSupplier> callForProduct(String param) throws Exception {
    String url = config.getEndpoint() + "?fetch-suppliers=true&ids=" + param;
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    HttpEntity<Void> request = new HttpEntity<>(headers);
    try {
      // This next line throws if Product not found at ProductMS or if fails to parse.
      List<ProductSupplier> productList =
          this.restTemplate
              .exchange(
                  url,
                  HttpMethod.GET,
                  request,
                  new ParameterizedTypeReference<List<ProductSupplier>>() {})
              .getBody();
      if (productList == null) {
        throw new AmqpRejectAndDontRequeueException("Product not found!");
      }
      log.info("ProductMS Response with Products: {}", productList.toString());
      return productList;
    } catch (HttpClientErrorException.Unauthorized unauthorized) {
      log.error(unauthorized.getMessage());
      throw new ExpiredTokenException("Expired!");
    } catch (ResourceAccessException resource) {
      // Handles connection refused with ProductMS
      // It could be a good idea to implement a retry policy or backoff here.
      log.error(resource.getMessage());
      throw new ApiIsDownException("ProductMS");
    } catch (RestClientException rest) {
      // Handles ProductMs errors like 400 / 404 / 500 etc
      log.error(rest.getMessage());
      throw new AmqpRejectAndDontRequeueException("Product not found!");
    }
  }

  private String getToken() throws JsonProcessingException {
    String payload =
        "{\"client_id\":\""
            + config.getClientId()
            + "\",\"client_secret\":\""
            + config.getClientSecret()
            + "\",\"audience\":\""
            + config.getAudience()
            + "\",\"grant_type\":\"client_credentials\"}";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<String>(payload, headers);

    String responseBody =
        this.restTemplate.postForObject(config.getTokenEndpoint(), request, String.class);
    JsonNode root = mapper.readTree(responseBody);
    return root.path("access_token").asText();
  }
}
