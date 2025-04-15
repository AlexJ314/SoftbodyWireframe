/** 
  * @file ArguedCallable.java
  * @brief NOT USED <br>
  * Allows a called thread to have some parameters
  * @author Alex Johnson
  */

import java.util.concurrent.Callable;
/** @class ArguedCallable
  * @brief parameters from called thread
  */
public abstract class ArguedCallable implements Callable<Object> {

  /// @param int i arbitrary integer thread parameter
  public int i = 0;

  /// @param int j arbitrary integer thread parameter
  public int j = 0;

  /** @brief Constructor with two parameters
    * @param int i arbitrary integer thread parameter
    * @param int j arbitrary integer thread parameter
    */
  ArguedCallable(int i, int j) {
    super();
    this.i = i;
    this.j = j;
  }

  /** @brief Constructor with one parameter
    * @param int i arbitrary integer thread parameter
    */
  ArguedCallable(int i) {
    super();
    this.i = i;
  }

  /** @brief Constructor with no parameters
    */
  ArguedCallable() {
    super();
  }
}
