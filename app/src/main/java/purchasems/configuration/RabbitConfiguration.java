package purchasems.configuration;

import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Setup rabbit broker. */
@AllArgsConstructor
@Configuration
public class RabbitConfiguration {

  @Bean
  public CachingConnectionFactory connectionFactory() {
    // ToDo: change to rabbitmq host at pipeline
    return new CachingConnectionFactory("localhost");
  }

  @Bean
  public RabbitAdmin amqpAdmin() {
    return new RabbitAdmin(connectionFactory());
  }

  @Bean
  public RabbitTemplate rabbitTemplate() {
    return new RabbitTemplate(connectionFactory());
  }

  @Bean
  public Queue customerQueue() {
    return new Queue("customer-purchase");
  }

  @Bean
  public Queue purchaseQueue() {
    return new Queue("purchase-customer");
  }

  @Bean
  public Queue adapterQueue() {
    return new Queue("adapter-purchase");
  }
}
