import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DisplayActions implements ActionListener {
  private Display display;
  private DisplayMouse displayMouse;

  public DisplayActions(Display display) {
    this.display = display;
  }

  public DisplayActions(DisplayMouse dm) {
    this.displayMouse = dm;
    this.display = dm.getDisplay();
  }

  public void actionPerformed(ActionEvent ae) {
    System.out.println("Overwrite me");
  }

  public Display getDisplay() {
    return this.display;
  }

  public void setDisplay(Display d) {
    this.display = d;
  }

  public DisplayMouse getDisplayMouse() {
    return this.displayMouse;
  }

  public void setDisplayMouse(DisplayMouse d) {
    this.displayMouse = d;
  }
}
