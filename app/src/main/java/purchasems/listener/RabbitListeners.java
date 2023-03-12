package purchasems.listener;

import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import purchasems.api.model.order.OrderRequest;
import purchasems.api.model.product.ProductSupplier;
import purchasems.exception.NotExistException;
import purchasems.service.OrderService;
import purchasems.service.ProductService;
import purchasems.service.PurchaseService;

/** Rabbit listener, works like a controller. */
@AllArgsConstructor
@Component
@Slf4j
public class RabbitListeners {

  private final OrderService orderService;
  private final ProductService productService;
  private final PurchaseService purchaseService;

  /**
   * Listen for customer-purchase queue.
   *
   * @param message Order request.
   * @throws NotExistException Not Exist Exception.
   */
  @RabbitListener(id = "customer", queues = "customer-purchase")
  public void listenCustomer(Message message) throws Exception {
    String correlation = message.getMessageProperties().getHeader("X-Correlation-Id");
    OrderRequest order =
        orderService.parseOrder(correlation, new String(message.getBody(), StandardCharsets.UTF_8));
    List<ProductSupplier> products = productService.getFromProductMs(order.getIds());
    purchaseService.splitAndSend(order, products, correlation);
  }

  /**
   * Listen for adapter-purchase queue.
   *
   * @param message Adapter request.
   */
  @RabbitListener(id = "adapter", queues = "adapter-purchase")
  public void listenAdapter(Message message) {
    purchaseService.updateStatus(
        message.getMessageProperties().getHeader("X-Correlation-Id"),
        new String(message.getBody(), StandardCharsets.UTF_8));
  }
}
