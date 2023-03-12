package purchasems;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/** Springboot main class. */
@SpringBootApplication
@EnableJpaRepositories
@ConfigurationPropertiesScan("purchasems.configuration")
public class PurchaseApplication {

  @PostConstruct
  void started() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  /**
   * Springboot main method.
   *
   * @param args CLI arguments.
   */
  public static void main(String[] args) {
    SpringApplication.run(PurchaseApplication.class, args);
  }
}
