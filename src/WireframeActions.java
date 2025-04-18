/** 
 * @file WireframeActions.java
 * @brief Overwritten class for sending events to the wireframe
 * @author Alex Johnson
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WireframeActions implements ActionListener {
  private Wireframe wireframe;

  public WireframeActions(Wireframe wireframe) {
    this.wireframe = wireframe;
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    System.out.println("Overwrite me");
  }

  public Wireframe getWireframe() {
    return this.wireframe;
  }

  public void setWireframe(Wireframe w) {
    this.wireframe = w;
  }
}
