package purchasems.exception;

import lombok.NoArgsConstructor;

/** Custom Exception to warn about extern apis not accessible. */
@NoArgsConstructor
public class ExpiredTokenException extends Exception {

  public ExpiredTokenException(String message) {
    super(message);
  }
}
