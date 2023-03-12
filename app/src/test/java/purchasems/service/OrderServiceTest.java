package purchasems.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import purchasems.api.model.order.OrderRequest;

public class OrderServiceTest {

  @Test
  public void testParseOrder_whenValid_thenLog() {
    OrderService orderService = new OrderService(new ObjectMapper());
    OrderRequest request =
        orderService.parseOrder(
            "correlation",
            "{\"order_id\":\"ORD0000001\",\"items\":[{\"quantity\":2,\"product\":{\"id\":\"PRD0000001\"}}]}");
    assertEquals("ORD0000001", request.getOrderId());
  }

  @Test
  public void testParseOrder_whenInvalid_thenThrow() {
    OrderService orderService = new OrderService(new ObjectMapper());
    assertThrows(
        AmqpRejectAndDontRequeueException.class,
        () ->
            orderService.parseOrder(
                "correlation",
                "{\"order_id\":\"invalid\",\"items\":[{\"quantity\":2,\"product\":{\"id\":\"PRD0000001\"}}]}"));
  }
}
