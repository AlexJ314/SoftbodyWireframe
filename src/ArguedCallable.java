/** 
 * @file ArguedCallable.java
 * @brief NOT USED <br>
 * Allows a called thread to have some parameters
 * @author Alex Johnson
 */

import java.util.concurrent.Callable;

public abstract class ArguedCallable implements Callable<Object> {
  public int i = 0;
  public int j = 0;

  ArguedCallable(int i, int j) {
    super();
    this.i = i;
    this.j = j;
  }

  ArguedCallable(int i) {
    super();
    this.i = i;
  }

  ArguedCallable() {
    super();
  }
}
