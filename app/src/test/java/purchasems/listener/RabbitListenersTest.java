package purchasems.listener;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import purchasems.PurchaseMsResponseUtil;
import purchasems.api.model.product.ProductSupplier;
import purchasems.service.OrderService;
import purchasems.service.ProductService;
import purchasems.service.PurchaseService;
import purchasems.util.Config;

@SpringBootTest(classes = {Config.class})
public class RabbitListenersTest {

  private RabbitListeners underTest;

  private static final String LISTENER_CONTAINER_ID = "customer";

  @Autowired private RabbitListenerTestHarness harness;
  @Autowired private TestRabbitTemplate testRabbitTemplate;

  @Mock(answer = RETURNS_DEEP_STUBS)
  private Message message;

  @MockBean private OrderService orderService;
  @MockBean private ProductService productService;
  @MockBean private PurchaseService purchaseService;

  @BeforeEach
  void setUp() {
    underTest = harness.getSpy(LISTENER_CONTAINER_ID);
    assertNotNull(underTest);
  }

  @AfterEach
  void tearDown() {
    reset(underTest);
  }

  @Test
  void testListenCustomer_whenMockMessage_ThenListen() throws Exception {
    given(
            orderService.parseOrder(
                "correlation",
                "{\"order_id\":\"ORD0000001\",\"items\":[{\"quantity\":2,\"product\":{\"id\":\"PRD0000001\"},{\"quantity\":3,\"product\":{\"id\":\"PRD0000003\"}}]}"))
        .willReturn(PurchaseMsResponseUtil.anotherOrderRequest());

    List<String> ids = new ArrayList<>();
    ids.add("PRD0000001");
    ids.add("PRD0000003");
    List<ProductSupplier> products = new ArrayList<>();
    products.add(PurchaseMsResponseUtil.defaultProductSupplier());
    products.add(PurchaseMsResponseUtil.anotherProductSupplier());

    given(productService.getFromProductMs(ids)).willReturn(products);

    MessageProperties mock = Mockito.mock(MessageProperties.class);
    when(message.getMessageProperties()).thenReturn(mock);
    when(mock.getHeader("X-Correlation-Id")).thenReturn("correlation");
    when(message.getBody())
        .thenReturn(
            "{\"order_id\":\"ORD0000001\",\"items\":[{\"quantity\":2,\"product\":{\"id\":\"PRD0000001\"},{\"quantity\":3,\"product\":{\"id\":\"PRD0000003\"}}]}"
                .getBytes(StandardCharsets.UTF_8));

    testRabbitTemplate.convertAndSend("customer-purchase", "customer-purchase", message);

    // verifies that all methods are called
    verify(purchaseService)
        .splitAndSend(PurchaseMsResponseUtil.anotherOrderRequest(), products, "correlation");
  }

  @Test
  void testListenAdapter_whenMockMessage_ThenListen() {
    doNothing()
        .when(purchaseService)
        .updateStatus("correlation", "{\"id\":\"SUA0000001\",\"purchase_id\":\"PUR0000001\"}");

    MessageProperties mock = Mockito.mock(MessageProperties.class);
    when(message.getMessageProperties()).thenReturn(mock);
    when(mock.getHeader("X-Correlation-Id")).thenReturn("correlation");
    when(message.getBody())
        .thenReturn(
            "{\"id\":\"SUA0000001\",\"purchase_id\":\"PUR0000001\"}"
                .getBytes(StandardCharsets.UTF_8));

    testRabbitTemplate.convertAndSend("adapter-purchase", "adapter-purchase", message);

    // verifies that the method is called
    verify(purchaseService)
        .updateStatus("correlation", "{\"id\":\"SUA0000001\",\"purchase_id\":\"PUR0000001\"}");
  }
}
