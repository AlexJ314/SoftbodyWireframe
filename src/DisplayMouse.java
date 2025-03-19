/** 
 * @file DisplayMouse.java
 * @brief Intercepts some mouse actions and turns them into display functions
 * @author Alex Johnson
 */

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;

public class DisplayMouse extends MouseAdapter {
  private Display display;
  private MouseEvent lastEvent = null;
  private MouseEvent lastClickEvent = null;
  private Point3[] selectedPoints;
  private int selectablePointsCount = 2;
  private double edgeLength = 1;
  private int edgeCount = 3;
  private int selectionMode = 0;
  private boolean addPoint = false;

  public DisplayMouse(Display display) {
    this.display = display;
    this.selectedPoints = new Point3[this.selectablePointsCount];
  }

  public void mouseDragged(MouseEvent e) {
    this.display.setFocusable(true);
    this.display.requestFocusInWindow();
    if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
      this.rotate(e);
    } else if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
      this.pan(e);
    } else if (e.getModifiersEx() == (MouseEvent.BUTTON1_DOWN_MASK |
      MouseEvent.CTRL_DOWN_MASK)) {
      this.grab(e);
    } else if (e.getModifiersEx() == (MouseEvent.BUTTON1_DOWN_MASK |
      MouseEvent.SHIFT_DOWN_MASK)) {
      this.select(e);
    }
  }

  public void mouseReleased(MouseEvent e) {
    this.lastEvent = null;
    if (this.selectionMode == 1) {
      this.selectionMode = 2;
    }
    this.display.ungrab(this.selectedPoints[0]);
  }

  public void mouseClicked(MouseEvent e) {
    this.lastClickEvent = e;
    if (this.addPoint == true) {
      this.addPoint(e.getX(), e.getY());
    } else {
      Point3 p = this.display.getPoint(e.getX(), e.getY());
      this.selectPoint(p);
    }
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    if (e.getWheelRotation() > 0) {
      this.display.zoomOut();
    }
    if (e.getWheelRotation() < 0) {
      this.display.zoomIn();
    }
  }

  public void selectPoint(Point3 p) {
    if (p == null) {
      return;
    }
    for (int i = 0; i < this.selectablePointsCount; i++) {
      if (p == this.selectedPoints[i]) {
        p.resetHighlight();
        for (int j = i; j < this.selectablePointsCount-1; j++) {
          this.selectedPoints[j] = this.selectedPoints[j+1];
          if (this.selectedPoints[j] != null) {
            this.selectedPoints[j].previousHighlight();
          }
        }
        this.selectedPoints[this.selectablePointsCount-1] = null;
        return;
      }
    }
    p.nextHighlight();
    if (this.selectedPoints[this.selectablePointsCount-1] != null) {
      this.selectedPoints[this.selectablePointsCount-1].resetHighlight();
    }
    for (int j = this.selectablePointsCount-1; j > 0; j--) {
      this.selectedPoints[j] = this.selectedPoints[j-1];
      if (this.selectedPoints[j] != null) {
        this.selectedPoints[j].nextHighlight();
      }
    }
    this.selectedPoints[0] = p;
  }

  public void addEdge() {
    if (this.selectionMode == 2) {
      int c = this.display.getWireframe().getPointCount();
      for (int i = 0; i < c; i++) {
        Point3 a = this.display.getWireframe().getPoint(i);
        for (int j = i+1; j < c; j++) {
          Point3 b = this.display.getWireframe().getPoint(j);
          if (this.display.getSelection().inBounds(a) &&
            this.display.getSelection().inBounds(b)) {
            if (this.display.getWireframe().removeConnection(a, b)) {
              this.display.getWireframe().addConnection(a, b, this.getEdgeLength());
            }
          }
        }
      }
    } else {
      Point3 a = this.selectedPoints[0];
      Point3 b = this.selectedPoints[1];
      if (a == null && b == null) {
        a = new Point3(0, 0, 0);
        a.jiggle();
        this.display.getWireframe().addPoint(a);
        b = new Point3(0, 0, 0);
        b.jiggle();
        this.display.getWireframe().addPoint(b);
      } else if (a == null) {
        a = new Point3(b);
        a.jiggle();
        this.display.getWireframe().addPoint(a);
      } else if (b == null) {
        b = new Point3(a);
        b.jiggle();
        this.display.getWireframe().addPoint(b);
      }
      this.display.getWireframe().addConnection(a, b, this.getEdgeLength());
    }
  }

  public void removeEdge() {
    if (this.selectionMode == 2) {
      int c = this.display.getWireframe().getPointCount();
      for (int i = 0; i < c; i++) {
        Point3 a = this.display.getWireframe().getPoint(i);
        for (int j = i+1; j < c; j++) {
          Point3 b = this.display.getWireframe().getPoint(j);
          if (this.display.getSelection().inBounds(a) &&
            this.display.getSelection().inBounds(b)) {
            this.display.getWireframe().removeConnection(a, b);
          }
        }
      }
    } else {
      this.display.getWireframe().removeConnection(this.selectedPoints[0], this.selectedPoints[1]);
    }
  }

  public void addPoint(int x, int y) {
    this.display.addPoint(x, y);
  }

  public void removePoint() {
    if (this.selectionMode == 2) {
      for (int i = 0; i < this.display.getWireframe().getPointCount(); i++) {
        if (this.display.getSelection().inBounds(this.display.getWireframe().getPoint(i))) {
          Point3 a = this.display.getWireframe().getPoint(i);
          for (int j = 0; j < this.selectablePointsCount; j++) {
            if (a == this.selectedPoints[j]) {
              this.selectPoint(a);
            }
          }
          this.display.getWireframe().removePoint(a);
          i--;
        }
      }
    } else {
      this.display.getWireframe().removePoint(this.selectedPoints[0]);
      this.selectPoint(this.selectedPoints[0]);
    }
  }

  public void bisect() {
    if (this.selectionMode == 2) {
      int c = this.display.getWireframe().getPointCount();
      for (int i = 0; i < c; i++) {
        Point3 a = this.display.getWireframe().getPoint(i);
        for (int j = i+1; j < c; j++) {
          Point3 b = this.display.getWireframe().getPoint(j);
          if (this.display.getSelection().inBounds(a) &&
            this.display.getSelection().inBounds(b)) {
            if (this.display.getWireframe().removeConnection(a, b)) {
              Point3 m = new Point3((a.getX()+b.getX())/2, (a.getY()+b.getY())/2, (a.getZ()+b.getZ())/2);
              this.display.getWireframe().addPoint(m);
              this.display.getWireframe().addConnection(m, a, this.getEdgeLength());
              this.display.getWireframe().addConnection(m, b, this.getEdgeLength());
            }
          }
        }
      }
    } else {
      this.removeEdge();
      Point3 a = this.selectedPoints[0];
      Point3 b = this.selectedPoints[1];
      if (a == null && b == null) {
        a = new Point3(0, 0, 0);
        a.jiggle();
        this.display.getWireframe().addPoint(a);
        b = new Point3(0, 0, 0);
        b.jiggle();
        this.display.getWireframe().addPoint(b);
      } else if (a == null) {
        a = new Point3(b);
        a.jiggle();
        this.display.getWireframe().addPoint(a);
      } else if (b == null) {
        b = new Point3(a);
        b.jiggle();
        this.display.getWireframe().addPoint(b);
      }
      if (a != null && b != null) {
        Point3 m = new Point3((a.getX()+b.getX())/2, (a.getY()+b.getY())/2, (a.getZ()+b.getZ())/2);
        this.display.getWireframe().addPoint(m);
        this.display.getWireframe().addConnection(m, a, this.getEdgeLength());
        this.display.getWireframe().addConnection(m, b, this.getEdgeLength());
      }
    }
  }

  public void pan(MouseEvent e) {
    if (this.lastEvent != null) {
      int dx = e.getX() - this.lastEvent.getX();
      int dy = e.getY() - this.lastEvent.getY();
      this.display.panBy(dx, dy);
    }
    this.lastEvent = e;
  }

  public void rotate(MouseEvent e) {
    if (this.lastEvent != null) {
      int dx = e.getX() - this.lastEvent.getX();
      int dy = e.getY() - this.lastEvent.getY();
      this.display.rotateBy(dx, dy);
    }
    this.lastEvent = e;
    if (this.selectionMode == 1) {
      this.selectionMode = 2;
    }
  }

  public void grab(MouseEvent e) {
    int dx = e.getX();
    int dy = e.getY();
    this.display.grab(this.selectedPoints[0], dx, dy);
    this.lastEvent = e;
  }

  public String setEdgeLength(String s) {
    if (s.equals("") == false) {
      try {
        this.setEdgeLength(Double.parseDouble(s));
        return String.valueOf(this.edgeLength);
      } catch (NumberFormatException e) {
        return "Edge length (" + String.valueOf(this.edgeLength) + ")";
      }
    }
    return "Edge length (" + String.valueOf(this.edgeLength) + ")";
  }

  public double setEdgeLength(double l) {
    if (l > 0) {
      this.edgeLength = l;
    }
    return this.edgeLength;
  }

  public double getEdgeLength() {
    return this.edgeLength;
  }

  public void lockPointToggle() {
    if (this.selectionMode == 2) {
      if (this.selectedPoints[0] != null) {
        boolean set = !this.selectedPoints[0].getLockToggle();
        for (int i = 0; i < this.display.getWireframe().getPointCount(); i++) {
          Point3 a = this.display.getWireframe().getPoint(i);
          if (this.display.getSelection().inBounds(a)) {
            a.setLock(set);
          }
        }
      } else {
        for (int i = 0; i < this.display.getWireframe().getPointCount(); i++) {
          Point3 a = this.display.getWireframe().getPoint(i);
          if (this.display.getSelection().inBounds(a)) {
            a.lockToggle();
          }
        }
      }
    } else {
      this.display.getWireframe().lockPointToggle(this.selectedPoints[0]);
    }
  }

  public boolean getLockPointToggle() {
    return this.display.getWireframe().getLockPointToggle(this.selectedPoints[0]);
  }

  public void addPolygon(int x, int y) {
    this.addPolygon(x, y, this.edgeCount, this.edgeLength);
  }

  public void addPolygon(int x, int y, int n) {
    this.addPolygon(x, y, n, this.edgeLength);
  }

  public void addPolygon(int x, int y, int n, double length) {
    if (this.selectedPoints[0] == null) {
      this.display.addLoosePolygon(x, y, n, length);
    } else if (this.selectedPoints[1] == null) {
      this.display.addConnectedPolygon(this.selectedPoints[0], n, length);
    } else {
      this.display.addConnectedPolygon(this.selectedPoints[0], this.selectedPoints[1], n, length);
    }
  }

  public String setEdgeCount(String s) {
    if (s.equals("") == false) {
      try {
        this.setEdgeCount(Integer.parseInt(s));
        return String.valueOf(this.edgeCount);
      } catch (NumberFormatException e) {
        return "Edge count (" + String.valueOf(this.edgeCount) + ")";
      }
    }
    return "Edge count (" + String.valueOf(this.edgeCount) + ")";
  }

  public int setEdgeCount(int n) {
    if (n >= 3) {
      this.edgeCount = n;
    }
    return this.edgeCount;
  }

  public int getEdgeCount() {
    return this.edgeCount;
  }

  public void mergePoints() {
    if (this.selectionMode == 2) {
      Point3 merger = this.selectedPoints[0];
      for (int i = 0; i < this.display.getWireframe().getPointCount(); i++) {
        Point3 a = this.display.getWireframe().getPoint(i);
        if (this.display.getSelection().inBounds(a)) {
          if (merger == null) {
            merger = a;
          }
          if (a != merger) {
            for (int j = 0; j < this.selectablePointsCount; j++) {
              if (a == this.selectedPoints[j]) {
                this.selectPoint(this.selectedPoints[j]);
              }
            }
            this.display.getWireframe().mergePoints(merger, a);
            i--;
          }
        }
      }
    } else {
      if (this.selectedPoints[0] != null && this.selectedPoints[1] != null) {
        this.display.getWireframe().mergePoints(this.selectedPoints[0], this.selectedPoints[1]);
        this.selectPoint(this.selectedPoints[1]);
      }
    }
  }

  public Display getDisplay() {
    return this.display;
  }

  public void setDisplay(Display d) {
    this.display = d;
  }

  public MouseEvent getLastEvent() {
    return this.lastEvent;
  }

  public void setLastEvent(MouseEvent e) {
    this.lastEvent = e;
  }

  public MouseEvent getLastClickEvent() {
    return this.lastClickEvent;
  }

  public void setLastClickEvent(MouseEvent e) {
    this.lastClickEvent = e;
  }

  public void select(MouseEvent e) {
    if (this.selectionMode == 0) {
      this.display.initialSelection(e.getX(), e.getY());
      this.selectionMode = 1;
    } else if (this.selectionMode == 1) {
      this.display.modifyInitialSelection(e.getX(), e.getY());
    } else {
      this.display.modifySelection(e.getX(), e.getY());
    }
  }

  public void deselect() {
    this.selectionMode = 0;
    this.display.deselect();
  }

  public void addPointToggle() {
    this.addPoint = !this.addPoint;
  }

  public void reset() {
    this.display.reset();
  }

  public void copy() {
    this.display.copy();
  }

  public void paste() {
    if (this.selectionMode == 2) {
      this.display.paste(this.display.getSelection().getBounds()[0]);
    } else if (this.lastClickEvent != null) {
      this.display.paste(this.display.get3d(this.lastClickEvent.getX(), this.lastClickEvent.getY()));
    } else {
      this.display.paste(this.display.getWireframe().midpoint());
    }
  }
}
