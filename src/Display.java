/** 
 * @file Display.java
 * @brief How to display the simulation to the screen
 * @author Alex Johnson
 */

import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GradientPaint;
import java.awt.BasicStroke;

/** @class Display
  * @brief How the simulation is displayed to the screen
  */
public class Display extends JPanel {

  /// @param 
  private Wireframe wireframe;

  /// @param 
  private Vector3 x = new Vector3(-Math.sqrt(3), Math.sqrt(3), 0).mul(1/Math.sqrt(6));

  /// @param 
  private Vector3 y = new Vector3(1, 1, -2).mul(1/Math.sqrt(6));

  /// @param 
  private Vector3 z = new Vector3(Math.sqrt(2), Math.sqrt(2), Math.sqrt(2)).mul(1/Math.sqrt(6));

  /// @param 
  private int width = Constants.panelWidth;

  /// @param 
  private int height = Constants.panelHeight;

  /// @param 
  private int panX = 0;

  /// @param 
  private int panY = 0;

  /// @param 
  private Vector3 lockAxis = null;

  /// @param 
  private boolean showStress = false;

  /// @param 
  private Selection selection = new Selection(this);

  /// @param 
  private boolean selecting = false;

  /// @param 
  private Vector3 xi = new Vector3(-3*Math.sqrt(2), 3*Math.sqrt(2), 0).mul(1.0/6.0);

  /// @param 
  private Vector3 yi = new Vector3(Math.sqrt(6), Math.sqrt(6), -2*Math.sqrt(6)).mul(1.0/6.0);

  /// @param 
  private Vector3 zi = new Vector3(2*Math.sqrt(3), 2*Math.sqrt(3), 2*Math.sqrt(3)).mul(1.0/6.0);


  /** @brief 
    * @param 
    * @return 
    */
  public Display(Wireframe wireframe) {
    super();
    this.setBackground(Color.WHITE);
    this.setSize(this.width, this.height);
    this.setVisible(true);
    this.wireframe = wireframe;
  }


  /** @brief 
    * @param 
    * @return 
    */
  public void setWidth(int w) {
    if (w > 0) {
      this.width = w;
    }
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void setHeight(int h) {
    if (h > 0) {
      this.height = h;
    }
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void setLock(Vector3 v) {
    this.lockAxis = v;
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void setShowStress(boolean b) {
    this.showStress = b;
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void addPoint(int sx, int sy) {
    this.wireframe.addPoint(new Point3(this.get3d(sx, sy)));
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void addLoosePolygon(int sx, int sy, int n, double length) {
    this.wireframe.addLoosePolygon(new Point3(this.get3d(sx, sy)), n, length);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void addConnectedPolygon(Point3 p, int n, double length) {
    this.wireframe.addConnectedPolygon(p, n, length);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void addConnectedPolygon(Point3 p, Point3 q, int n, double length) {
    this.wireframe.addConnectedPolygon(p, q, n, length);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.clearRect(0, 0, this.width, this.height);
    if (this.wireframe.getPointCount() > 0) {
      double minDepth = this.fproject(this.wireframe.getPoint(0)).getZ();
      double maxDepth = minDepth;
      for (int i = 0; i < this.wireframe.getPointCount(); i++) {
        double d = this.fproject(this.wireframe.getPoint(i)).getZ();
        if (d > maxDepth) {
          maxDepth = d;
        }
        if (d < minDepth) {
          minDepth = d;
        }
      }
      if (this.showStress == true) {
        double minStress = 0;
        double maxStress = 0;
        for (int i = 0; i < this.wireframe.getEdgeCount(); i++) {
          double s = this.wireframe.getEdge(i).getPointA()
            .distance(this.wireframe.getEdge(i).getPointB()) -
            this.wireframe.getEdge(i).getLength();
          if (s > maxStress) {
            maxStress = s;
          }
          if (s < minStress) {
            minStress = s;
          }
        }
        for (int i = 0; i < this.wireframe.getEdgeCount(); i++) {
          this.line(g, this.wireframe.getEdge(i), minStress, maxStress);
        }
      } else {
        for (int i = 0; i < this.wireframe.getEdgeCount(); i++) {
          this.line(g, this.wireframe.getEdge(i), minDepth, maxDepth);
        }
      }
      for (int i = 0; i < this.wireframe.getPointCount(); i++) {
        this.dot(g, this.wireframe.getPoint(i), minDepth, maxDepth);
      }
    }
    this.orientation(g);
    this.drawSelection(g);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void line(Graphics g, Edge e, double min, double max) {
    Vector3 va = this.project(e.getPointA());
    Vector3 vb = this.project(e.getPointB());
    if (this.showStress == true) {
      double c = 1;
      double s = e.getPointA().distance(e.getPointB()) - e.getLength();
      if (s < 0) {  // Blue
        g.setColor(new Color(0, 0, (float) Math.min(Math.max(Math.sqrt(Math.abs(s))/Math.sqrt(Math.abs(min)), 0), 1)));
      } else if (s > 0) {  // Red
        g.setColor(new Color((float) Math.min(Math.max(Math.sqrt(s)/Math.sqrt(max), 0), 1), 0, 0));
      } else {  // Black
        g.setColor(new Color(0, 0, 0));
      }
      g.drawLine((int) Math.round(va.getX()), (int) Math.round(va.getY()),
        (int) Math.round(vb.getX()), (int) Math.round(vb.getY()));
    } else {
      double da = va.getZ();
      double db = vb.getZ();
      da -= min;
      db -= min;
      max -= min;
      da /= max;
      db /= max;
      da *= 0.75;
      db *= 0.75;
      da = 0.75-da;
      db = 0.75-db;
      da = Math.min(Math.max(da, 0), 1);
      db = Math.min(Math.max(db, 0), 1);
      Graphics2D g2 = (Graphics2D) g;
      GradientPaint gp = new GradientPaint((float) va.getX(), (float) va.getY(),
        new Color((float) da, (float) da, (float) da),
        (float) vb.getX(), (float) vb.getY(),
        new Color((float) db, (float) db, (float) db));
      g2.setPaint(gp);
      g2.setStroke(new BasicStroke(1));
      g2.drawLine((int) Math.round(va.getX()), (int) Math.round(va.getY()),
        (int) Math.round(vb.getX()), (int) Math.round(vb.getY()));
    }
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void dot(Graphics g, Point3 p, double min, double max) {
    boolean draw = false;
    if (p.getHighlight() > 0) {
      switch (p.getHighlight()) {
        case 1: g.setColor(Color.GREEN);
          break;
        case 2: g.setColor(Color.BLUE);
          break;
        default: g.setColor(Color.RED);
          break;
      }
      draw = true;
    } else if (this.selecting == true && this.selection.inBounds(p) == true) {
      g.setColor(Color.CYAN);
      draw = true;
    } else if (p.getConnectionCount() == 0) {
      g.setColor(Color.MAGENTA);
      draw = true;
    } else if (p.getLockToggle() == true) {
      g.setColor(Color.ORANGE);
      draw = true;
    }
    if (draw == true) {
      Vector3 v = this.project(p);
      int vx = (int) Math.round(v.getX()-Constants.pointSize/2);
      int vy = (int) Math.round(v.getY()-Constants.pointSize/2);
      double vz = v.getZ();
      vz -= min;
      max -= min;
      vz /= max;
      vz *= 0.25;
      vz += 0.75;
      vz = Math.min(Math.max(vz, 0), 1);
      float[] c = new float[4];
      g.getColor().getComponents(c);
      g.setColor(new Color((float) vz*c[0], (float) vz*c[1], (float) vz*c[2]));
      g.fillOval(vx, vy, Constants.pointSize, Constants.pointSize);
    }
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void orientation(Graphics g) {
    g.setColor(Color.RED);
    g.drawLine(this.width-50, this.height-50,
      (int) Math.round(this.x.norm().getX()*20+this.width-50),
      (int) Math.round(this.y.norm().getX()*20+this.height-50));
    g.setColor(Color.GREEN);
    g.drawLine(this.width-50, this.height-50,
      (int) Math.round(this.x.norm().getY()*20+this.width-50),
      (int) Math.round(this.y.norm().getY()*20+this.height-50));
    g.setColor(Color.BLUE);
    g.drawLine(this.width-50, this.height-50,
      (int) Math.round(this.x.norm().getZ()*20+this.width-50),
      (int) Math.round(this.y.norm().getZ()*20+this.height-50));
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void orientationCenter(Graphics g) {
    g.setColor(Color.RED);
    g.drawLine(this.width/2+this.panX, this.height/2+this.panY,
      (int) Math.round(this.x.norm().getX()*20+this.width/2+this.panX),
      (int) Math.round(this.y.norm().getX()*20+this.height/2+this.panY));
    g.setColor(Color.GREEN);
    g.drawLine(this.width/2+this.panX, this.height/2+this.panY,
      (int) Math.round(this.x.norm().getY()*20+this.width/2+this.panX),
      (int) Math.round(this.y.norm().getY()*20+this.height/2+this.panY));
    g.setColor(Color.BLUE);
    g.drawLine(this.width/2+this.panX, this.height/2+this.panY,
      (int) Math.round(this.x.norm().getZ()*20+this.width/2+this.panX),
      (int) Math.round(this.y.norm().getZ()*20+this.height/2+this.panY));
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void reset() {
    this.x = new Vector3(-Math.sqrt(3), Math.sqrt(3), 0).mul(1/Math.sqrt(6));
    this.y = new Vector3(1, 1, -2).mul(1/Math.sqrt(6));
    this.z = new Vector3(Math.sqrt(2), Math.sqrt(2), Math.sqrt(2)).mul(1/Math.sqrt(6));
    this.panX = 0;
    this.panY = 0;
    this.xi = new Vector3(-3*Math.sqrt(2), 3*Math.sqrt(2), 0).mul(1.0/6.0);
    this.yi = new Vector3(Math.sqrt(6), Math.sqrt(6), -2*Math.sqrt(6)).mul(1.0/6.0);
    this.zi = new Vector3(2*Math.sqrt(3), 2*Math.sqrt(3), 2*Math.sqrt(3)).mul(1.0/6.0);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void zoomIn() {
    this.x = this.x.mul(1.1);
    this.y = this.y.mul(1.1);
    this.z = this.z.mul(1.1);
    this.xi = this.xi.mul(1/1.1);
    this.yi = this.yi.mul(1/1.1);
    this.zi = this.zi.mul(1/1.1);
    this.panX *= 1.1;
    this.panY *= 1/1.1;
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void zoomOut() {
    this.x = this.x.mul(1/1.1);
    this.y = this.y.mul(1/1.1);
    this.z = this.z.mul(1/1.1);
    this.xi = this.xi.mul(1.1);
    this.yi = this.yi.mul(1.1);
    this.zi = this.zi.mul(1.1);
    this.panX *= 1/1.1;
    this.panY *= 1/1.1;
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void panBy(int px, int py) {
    this.panX += px;
    this.panY += py;
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void rotateBy(double dx, double dy) {
    double theta = -Math.PI*Math.max(Math.min(2*Math.sqrt(dx*dx + dy*dy)/
      Math.sqrt(this.width*this.width+this.height*this.height), 1), -1);
    Vector3 u = this.getLock();
    if (u != null) {
      double xsign = 1;
      double ysign = 1;
      if (dx < 0) {
        xsign = -1;
      }
      if (dy < 0) {
        ysign = -1;
      }
      if (xsign*dx*dx + ysign*dy*dy < 0) {
        u = u.mul(-1);
      }
    } else {
      u = this.get3d(
        dy+7+this.width/2+this.panX,
        -dx+30+this.height/2+this.panY,
        0
      ).norm();
    }
    this.rotate(u, theta);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void rotate(Vector3 u, double theta) {
    Vector3 ra = new Vector3(
      Math.pow(u.getX(), 2)*(1-Math.cos(theta))+Math.cos(theta),
      u.getX()*u.getY()*(1-Math.cos(theta))-u.getZ()*Math.sin(theta),
      u.getX()*u.getZ()*(1-Math.cos(theta))+u.getY()*Math.sin(theta)
    );
    Vector3 rb = new Vector3(
      u.getX()*u.getY()*(1-Math.cos(theta))+u.getZ()*Math.sin(theta),
      Math.pow(u.getY(), 2)*(1-Math.cos(theta))+Math.cos(theta),
      u.getY()*u.getZ()*(1-Math.cos(theta))-u.getX()*Math.sin(theta)
    );
    Vector3 rc = new Vector3(
      u.getX()*u.getZ()*(1-Math.cos(theta))-u.getY()*Math.sin(theta),
      u.getY()*u.getZ()*(1-Math.cos(theta))+u.getX()*Math.sin(theta),
      Math.pow(u.getZ(), 2)*(1-Math.cos(theta))+Math.cos(theta)
    );
    double a = this.x.dot(ra);
    double b = this.x.dot(rb);
    double c = this.x.dot(rc);
    this.x = new Vector3(a, b, c);
    a = this.y.dot(ra);
    b = this.y.dot(rb);
    c = this.y.dot(rc);
    this.y = new Vector3(a, b, c);
    a = this.z.dot(ra);
    b = this.z.dot(rb);
    c = this.z.dot(rc);
    this.z = new Vector3(a, b, c);

    u = u.mul(-1);
    ra = new Vector3(
      Math.pow(u.getX(), 2)*(1-Math.cos(theta))+Math.cos(theta),
      u.getX()*u.getY()*(1-Math.cos(theta))+u.getZ()*Math.sin(theta),
      u.getX()*u.getZ()*(1-Math.cos(theta))-u.getY()*Math.sin(theta)
    );
    rb = new Vector3(
      u.getX()*u.getY()*(1-Math.cos(theta))-u.getZ()*Math.sin(theta),
      Math.pow(u.getY(), 2)*(1-Math.cos(theta))+Math.cos(theta),
      u.getY()*u.getZ()*(1-Math.cos(theta))+u.getX()*Math.sin(theta)
    );
    rc = new Vector3(
      u.getX()*u.getZ()*(1-Math.cos(theta))+u.getY()*Math.sin(theta),
      u.getY()*u.getZ()*(1-Math.cos(theta))-u.getX()*Math.sin(theta),
      Math.pow(u.getZ(), 2)*(1-Math.cos(theta))+Math.cos(theta)
    );
    a = this.xi.dot(ra);
    b = this.xi.dot(rb);
    c = this.xi.dot(rc);
    this.xi = new Vector3(a, b, c);
    a = this.yi.dot(ra);
    b = this.yi.dot(rb);
    c = this.yi.dot(rc);
    this.yi = new Vector3(a, b, c);
    a = this.zi.dot(ra);
    b = this.zi.dot(rb);
    c = this.zi.dot(rc);
    this.zi = new Vector3(a, b, c);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void grab(Point3 p, int gx, int gy) {
    if (p != null) {
      Vector3 v = this.get3d(gx, gy, this.fproject(p).getZ());
      p.setGrabbed(true);
      p.setX(v.getX());
      p.setY(v.getY());
      p.setZ(v.getZ());
    }
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void ungrab(Point3 p) {
    if (p != null) {
      p.setGrabbed(false);
    }
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void drawSelection(Graphics g) {
    if (this.selecting == false) {
      return;
    }
    Vector3[] bounds = this.selection.getBounds();

    Vector3 ooo = this.project(new Vector3(bounds[0]));
    Vector3 poo = this.project(new Vector3(bounds[1]));
    Vector3 opo = this.project(new Vector3(bounds[2]));
    Vector3 ppo = this.project(new Vector3(bounds[3]));
    Vector3 oop = this.project(new Vector3(bounds[4]));
    Vector3 pop = this.project(new Vector3(bounds[5]));
    Vector3 opp = this.project(new Vector3(bounds[6]));
    Vector3 ppp = this.project(new Vector3(bounds[7]));

    g.setColor(new Color((float) 0.3, (float) 0.3, (float) 1.0, (float) 0.2));

    g.drawPolygon(new int[] {(int) Math.round(oop.getX()), (int) Math.round(pop.getX()),
      (int) Math.round(ppp.getX()), (int) Math.round(opp.getX())},
      new int[] {(int) Math.round(oop.getY()), (int) Math.round(pop.getY()),
      (int) Math.round(ppp.getY()), (int) Math.round(opp.getY())}, 4);
    g.drawPolygon(new int[] {(int) Math.round(ooo.getX()), (int) Math.round(oop.getX()),
    (int) Math.round(opp.getX()), (int) Math.round(opo.getX())},
      new int[] {(int) Math.round(ooo.getY()), (int) Math.round(oop.getY()),
      (int) Math.round(opp.getY()), (int) Math.round(opo.getY())}, 4);
    g.drawPolygon(new int[] {(int) Math.round(opo.getX()), (int) Math.round(opp.getX()),
    (int) Math.round(ppp.getX()), (int) Math.round(ppo.getX())},
      new int[] {(int) Math.round(opo.getY()), (int) Math.round(opp.getY()),
      (int) Math.round(ppp.getY()), (int) Math.round(ppo.getY())}, 4);
    g.drawPolygon(new int[] {(int) Math.round(opo.getX()), (int) Math.round(opp.getX()),
    (int) Math.round(ppp.getX()), (int) Math.round(ppo.getX())},
      new int[] {(int) Math.round(opo.getY()), (int) Math.round(opp.getY()),
      (int) Math.round(ppp.getY()), (int) Math.round(ppo.getY())}, 4);
    g.drawPolygon(new int[] {(int) Math.round(ppo.getX()), (int) Math.round(ppp.getX()),
    (int) Math.round(pop.getX()), (int) Math.round(poo.getX())},
      new int[] {(int) Math.round(ppo.getY()), (int) Math.round(ppp.getY()),
      (int) Math.round(pop.getY()), (int) Math.round(poo.getY())}, 4);
    g.drawPolygon(new int[] {(int) Math.round(ooo.getX()), (int) Math.round(poo.getX()),
    (int) Math.round(ppo.getX()), (int) Math.round(opo.getX())},
      new int[] {(int) Math.round(ooo.getY()), (int) Math.round(poo.getY()),
      (int) Math.round(ppo.getY()), (int) Math.round(opo.getY())}, 4);

    g.fillPolygon(new int[] {(int) Math.round(oop.getX()), (int) Math.round(pop.getX()),
    (int) Math.round(ppp.getX()), (int) Math.round(opp.getX())},
      new int[] {(int) Math.round(oop.getY()), (int) Math.round(pop.getY()),
      (int) Math.round(ppp.getY()), (int) Math.round(opp.getY())}, 4);
    g.fillPolygon(new int[] {(int) Math.round(ooo.getX()), (int) Math.round(oop.getX()),
    (int) Math.round(opp.getX()), (int) Math.round(opo.getX())},
      new int[] {(int) Math.round(ooo.getY()), (int) Math.round(oop.getY()),
      (int) Math.round(opp.getY()), (int) Math.round(opo.getY())}, 4);
    g.fillPolygon(new int[] {(int) Math.round(ooo.getX()), (int) Math.round(oop.getX()),
    (int) Math.round(pop.getX()), (int) Math.round(poo.getX())},
      new int[] {(int) Math.round(ooo.getY()), (int) Math.round(oop.getY()),
      (int) Math.round(pop.getY()), (int) Math.round(poo.getY())}, 4);
    g.fillPolygon(new int[] {(int) Math.round(opo.getX()), (int) Math.round(opp.getX()),
    (int) Math.round(ppp.getX()), (int) Math.round(ppo.getX())},
      new int[] {(int) Math.round(opo.getY()), (int) Math.round(opp.getY()),
      (int) Math.round(ppp.getY()), (int) Math.round(ppo.getY())}, 4);
    g.fillPolygon(new int[] {(int) Math.round(ppo.getX()), (int) Math.round(ppp.getX()),
    (int) Math.round(pop.getX()), (int) Math.round(poo.getX())},
      new int[] {(int) Math.round(ppo.getY()), (int) Math.round(ppp.getY()),
      (int) Math.round(pop.getY()), (int) Math.round(poo.getY())}, 4);
    g.fillPolygon(new int[] {(int) Math.round(ooo.getX()), (int) Math.round(poo.getX()),
    (int) Math.round(ppo.getX()), (int) Math.round(opo.getX())},
      new int[] {(int) Math.round(ooo.getY()), (int) Math.round(poo.getY()),
      (int) Math.round(ppo.getY()), (int) Math.round(opo.getY())}, 4);
  }

    /** @brief 
    * @param 
    * @return 
    */
  public Vector3 get3d(double sx, double sy) {
    Vector3 mid = this.wireframe.midpoint();
    return this.get3d(sx, sy, this.project(mid).getZ());
  }

  /** @brief 
    * @param 
    * @return 
    */
  public Vector3 get3d(double sx, double sy, double sz) {
    sx -= (7+this.width/2+this.panX);
    sy -= (30+this.height/2+this.panY);
    sx /= Constants.lineScale;
    sy /= Constants.lineScale;
    sz /= Constants.lineScale;
    return new Vector3(
      this.xi.getX()*sx+this.yi.getX()*sy+this.zi.getX()*sz, 
      this.xi.getY()*sx+this.yi.getY()*sy+this.zi.getY()*sz, 
      this.xi.getZ()*sx+this.yi.getZ()*sy+this.zi.getZ()*sz
    );
  }

  /** @brief 
    * @param 
    * @return 
    */
  public Vector3 project(Point3 p) {
    return this.project(new Vector3(p));
  }

  /** @brief 
    * @param 
    * @return 
    */
  public Vector3 project(Vector3 v) {
    int vx = (int) Math.round(this.x.dot(v)*Constants.lineScale+this.width/2+this.panX);
    int vy = (int) Math.round(this.y.dot(v)*Constants.lineScale+this.height/2+this.panY);
    int vz = (int) Math.round(this.z.dot(v)*Constants.lineScale);
    return new Vector3(vx, vy, vz);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public Vector3 fproject(Point3 p) {
    return this.fproject(new Vector3(p));
  }

  /** @brief 
    * @param 
    * @return 
    */
  public Vector3 fproject(Vector3 v) {
    double vx = this.x.dot(v)*Constants.lineScale+this.width/2+this.panX;
    double vy = this.y.dot(v)*Constants.lineScale+this.height/2+this.panY;
    double vz = this.z.dot(v)*Constants.lineScale;
    return new Vector3(vx, vy, vz);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public Point3 getPoint(int screenX, int screenY) {
    screenX -= 7;
    screenY -= 30;
    Point3 point = null;
    if (this.wireframe.getPointCount() > 0) {
      point = this.wireframe.getPoint(0);
      Vector3 projection = this.fproject(point);
      double d = Math.sqrt(Math.pow(projection.getX()-screenX, 2) + Math.pow(projection.getY()-screenY, 2));
      for (int i = 1; i < this.wireframe.getPointCount(); i++) {
        Point3 q = this.wireframe.getPoint(i);
        Vector3 j = this.fproject(q);
        double t = Math.sqrt(Math.pow(j.getX()-screenX, 2) + Math.pow(j.getY()-screenY, 2));
        if (t < d) {
          point = q;
          projection = j;
          d = t;
        } else if (t == d) {
          if (projection.getZ() <= j.getZ()) {
            point = q;
            projection = j;
          }
        }
      }
    }
    return point;
  }

  /** @brief 
    * @param 
    * @return 
    */
  public Wireframe getWireframe() {
    return this.wireframe;
  }

  /** @brief 
    * @param 
    * @return 
    */
  public Vector3 getLock() {
    return this.lockAxis;
  }

  /** @brief 
    * @param 
    * @return 
    */
  public boolean showStressToggle() {
    return this.showStress = !this.showStress;
  }

  /** @brief 
    * @param 
    * @return 
    */
  public boolean getShowStress() {
    return this.showStress;
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void initialSelection(int sx, int sy) {
    this.selecting = true;
    this.selection.setStart(sx, sy);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void modifyInitialSelection(int sx, int sy) {
    this.selecting = true;
    this.selection.modifyInitial(sx, sy);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void modifySelection(int sx, int sy) {
    this.selecting = true;
    this.selection.modifySelection(sx, sy);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void deselect() {
    this.selecting = false;
    this.selection.deselect();
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void copy() {
    this.selection.copy(this.wireframe);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public void paste(Vector3 offset) {
    this.selection.paste(this.wireframe, offset);
  }

  /** @brief 
    * @param 
    * @return 
    */
  public Selection getSelection() {
    return this.selection;
  }
}
