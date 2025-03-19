import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WireframeActions implements ActionListener {
  private Wireframe wireframe;

  public WireframeActions(Wireframe wireframe) {
    this.wireframe = wireframe;
  }

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
