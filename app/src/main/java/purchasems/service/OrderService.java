package purchasems.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Service;
import purchasems.api.model.order.OrderRequest;
import purchasems.util.JavaxValidator;

/** Order Services. */
@AllArgsConstructor
@Service
@Slf4j
public class OrderService {

  private final ObjectMapper mapper;

  /**
   * Parses the JMS message into an Order Request object.
   *
   * @param correlation Correlation Id.
   * @param message JMS message.
   * @return Order Request.
   */
  public OrderRequest parseOrder(String correlation, String message) {
    log.info("Message received with Correlation: {} | Body: {}", correlation, message);
    try {
      OrderRequest request = mapper.readerFor(OrderRequest.class).readValue(message);
      JavaxValidator.validate(request);
      log.info("Successfully parsed the message into the object: {}", request);
      return request;
    } catch (Exception e) {
      log.error("Poisoned message! error: {}", e.getMessage());
      throw new AmqpRejectAndDontRequeueException("Poisoned Message");
    }
  }
}
