package purchasems.exception;

import lombok.NoArgsConstructor;

/** Custom Exception to warn about non-existing objects. */
@NoArgsConstructor
public class NotExistException extends Exception {

  public NotExistException(String objectName) {
    super(objectName + " does not exist!");
  }
}
