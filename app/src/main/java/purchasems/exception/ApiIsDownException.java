package purchasems.exception;

import lombok.NoArgsConstructor;

/** Custom Exception to warn about extern apis not accessible. */
@NoArgsConstructor
public class ApiIsDownException extends Exception {

  public ApiIsDownException(String apiName) {
    super(apiName + " is down!");
  }
}
